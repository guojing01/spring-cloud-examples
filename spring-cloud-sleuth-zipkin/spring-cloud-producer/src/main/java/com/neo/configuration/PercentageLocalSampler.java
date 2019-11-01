package com.neo.configuration;

import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.Span;

import java.util.BitSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
* @Author rcb
* @Description 通过正则配置自定义采样率
**/
public class PercentageLocalSampler implements Sampler {

    private final Map<String, BitSet> sampleDecisionsMap;
    private final SamplerLocalProperties configuration;
    private final String all = "all";
    private final Map<String, AtomicInteger> concurrentSampleCount;

    public Map<String, AtomicInteger> getConcurrentSampleCount(){
        return this.concurrentSampleCount;
    }

    public PercentageLocalSampler(SamplerLocalProperties configuration) {
        this.configuration = configuration;
        sampleDecisionsMap = buildRandomBit();
        concurrentSampleCount = new ConcurrentHashMap<>();
        // 设置全局的上报次数
        concurrentSampleCount.put(all, new AtomicInteger(0));
    }


    @Override
    public boolean isSampled(Span currentSpan) {
        if (currentSpan == null) {
            return false;
        }

        String uri = currentSpan.getName(); // 获取span中的请求uri
        uri = uri.replace("http://", "");
        AtomicInteger count = this.concurrentSampleCount.get(all); // 获取全局的访问率
        BitSet bitSet =  this.sampleDecisionsMap.get(all);  // 获取全局的bitSet
        float percentage = this.configuration.getPercentage();  // 获取全局的采样率
        for (UriSampleProperties sampleProperties : configuration.getUriSample()) {
            //匹配上了自定义采样率的正则
            if (uri.contains(sampleProperties.getUriRegex())) {
                synchronized (this){ // 多个线程会有并发问题，这里加个局部锁
                    if (!concurrentSampleCount.containsKey(uri)) { // 判断当前uri是否在map中
                        concurrentSampleCount.put(uri, new AtomicInteger(0));
                    }
                }
                count = concurrentSampleCount.get(uri); // 获取当前URI对应的访问次数
                bitSet = sampleDecisionsMap.get(sampleProperties.getUriRegex()); // 获取当前URI对应的bitSet
                percentage = sampleProperties.getUriPercentage(); // 获取当前URI对应的采样率
            }
        }


         if(percentage == 0.0f){ // 如果采样率是0 ，直接返回false
            return false;
        }else if (percentage == 1.0f){ // 如果采样率是1 ，那么直接返回true
             return true;
         }
        synchronized (this) {
            final int i = count.getAndIncrement(); // f访问次数加1
            boolean result = bitSet.get(i); // 判断当前的访问 次数是否在 bitSet中，存在则返回true
            if (i == 99) { // 等于99的时候，重新设置为0 
                count.set(0);
            }
            return result;
        }
    }

    /**
     * Reservoir sampling algorithm borrowed from Stack Overflow.
     * <p>
     * http://stackoverflow.com/questions/12817946/generate-a-random-bitset-with-n-1s
     */
    static BitSet randomBitSet(int size, int cardinality, Random rnd) {
        BitSet result = new BitSet(size);
        int[] chosen = new int[cardinality];
        int i;
        for (i = 0; i < cardinality; ++i) {
            chosen[i] = i;
            result.set(i);
        }
        for (; i < size; ++i) {
            int j = rnd.nextInt(i + 1);
            if (j < cardinality) {
                result.clear(chosen[j]);
                result.set(i);
                chosen[j] = i;
            }
        }
        return result;
    }

    private Map<String, BitSet> buildRandomBit() {
        Map<String, BitSet> map = new ConcurrentHashMap<>();
        // 设置全局的采样率
        int outOf100 = (int) (configuration.getPercentage() * 100.0f);
        map.put(all, randomBitSet(100, outOf100, new Random()));
        if (null!= configuration.getUriSample()) {
            for (UriSampleProperties sampleProperties : configuration.getUriSample()) {
                // 设置个性化的采样率
                map.put(sampleProperties.getUriRegex(), randomBitSet(100,
                        (int) (sampleProperties.getUriPercentage() * 100.0f), new Random()));
            }
        }
        return map;
    }
}