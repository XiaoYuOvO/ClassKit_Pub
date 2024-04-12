package net.xiaoyu233.classkit.util.profiler;

import java.util.function.Supplier;

public interface Profiler {
   String ROOT_NAME = "root";

   void startTick();

   void endTick();

   void push(String pLocation);

   void push(Supplier<String> pLocationGetter);

   void pop();

   void swap(String pLocation);

   void swap(Supplier<String> pLocationGetter);

   void markSampleType(SampleType pType);

   default void visit(String pMarker) {
      this.visit(pMarker, 1);
   }

   void visit(String pMarker, int pP_185259_);

   default void visit(Supplier<String> pMarkerGetter) {
      this.visit(pMarkerGetter, 1);
   }

   void visit(Supplier<String> pMarkerGetter, int pP_185261_);

   static Profiler union(final Profiler pA, final Profiler pB) {
      if (pA == DummyProfiler.INSTANCE) {
         return pB;
      } else {
         return pB == DummyProfiler.INSTANCE ? pA : new Profiler() {
            public void startTick() {
               pA.startTick();
               pB.startTick();
            }

            public void endTick() {
               pA.endTick();
               pB.endTick();
            }

            public void push(String pP_18594_) {
               pA.push(pP_18594_);
               pB.push(pP_18594_);
            }

            public void push(Supplier<String> pP_18596_) {
               pA.push(pP_18596_);
               pB.push(pP_18596_);
            }

            public void markSampleType(SampleType pP_145961_) {
               pA.markSampleType(pP_145961_);
               pB.markSampleType(pP_145961_);
            }

            public void pop() {
               pA.pop();
               pB.pop();
            }

            public void swap(String pP_18599_) {
               pA.swap(pP_18599_);
               pB.swap(pP_18599_);
            }

            public void swap(Supplier<String> pP_18601_) {
               pA.swap(pP_18601_);
               pB.swap(pP_18601_);
            }

            public void visit(String pP_185263_, int pP_185264_) {
               pA.visit(pP_185263_, pP_185264_);
               pB.visit(pP_185263_, pP_185264_);
            }

            public void visit(Supplier<String> pP_185266_, int pP_185267_) {
               pA.visit(pP_185266_, pP_185267_);
               pB.visit(pP_185266_, pP_185267_);
            }
         };
      }
   }
}
