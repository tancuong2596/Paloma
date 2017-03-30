package cit.edu.paloma.misc;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class IdentifierGenerator {
        private AtomicInteger upperBound;
        private ConcurrentSkipListSet<Integer> numbersSet;

        public IdentifierGenerator() {
            upperBound = new AtomicInteger(0);
            numbersSet = new ConcurrentSkipListSet<>();
        }

        public synchronized Integer nextInt() {
            if (!numbersSet.isEmpty()) {
                for (Integer integer : numbersSet) {
                    numbersSet.remove(integer);
                    return integer;
                }
            }
            return upperBound.addAndGet(1);
        }

        public synchronized void putBackInt(Integer number) {
            numbersSet.add(number);
        }
    }
