package net.xiaoyu233.classkit.util.profiler;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public class DummyProfiler implements ReadableProfiler {
   public static final DummyProfiler INSTANCE = new DummyProfiler();

   private DummyProfiler() {
   }

   public void startTick() {
   }

   public void endTick() {
   }

   public void push(String pP_18559_) {
   }

   public void push(Supplier<String> pP_18561_) {
   }

   public void markSampleType(SampleType pP_145951_) {
   }

   public void pop() {
   }

   public void swap(String pP_18564_) {
   }

   public void swap(Supplier<String> pP_18566_) {
   }

   public void visit(String pP_185253_, int pP_185254_) {
   }

   public void visit(Supplier<String> pP_185256_, int pP_185257_) {
   }

   public ProfileResult getResult() {
      return EmptyProfileResult.INSTANCE;
   }

   @Nullable
   public ProfilerSystem.LocatedInfo getInfo(String pP_145953_) {
      return null;
   }

   public Set<Pair<String, SampleType>> getSampleTargets() {
      return ImmutableSet.of();
   }
}
