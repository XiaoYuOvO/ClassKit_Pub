package net.xiaoyu233.classkit.util.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.xiaoyu233.classkit.util.Utils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class ProfilerSystem implements ReadableProfiler {
   private long timeOutNanos = Duration.ofMillis(100L).toNanos();
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<String> path = Lists.newArrayList();
   private final Stack<String> pathStack = new Stack<>();
   private final LongList timeList = new LongArrayList();
   private final Map<String, LocatedInfo> locationInfos = Maps.newHashMap();
   private final IntSupplier endTickGetter;
   private final LongSupplier timeGetter;
   private final long startTime;
   private final int startTick;
   private String fullPath = "";
   private boolean tickStarted;
   @Nullable
   private LocatedInfo currentInfo;
   private final boolean checkTimeout;
   private int tickSpan;
   private final Set<Pair<String, SampleType>> sampleTypes = new ObjectArraySet<>();

   public ProfilerSystem(){
      this(System::nanoTime,false);
   }

   public ProfilerSystem(LongSupplier pTimeGetter, IntSupplier pTickGetter, boolean pCheckTimeout) {
      this.startTime = pTimeGetter.getAsLong();
      this.timeGetter = pTimeGetter;
      this.startTick = pTickGetter.getAsInt();
      this.endTickGetter = pTickGetter;
      this.checkTimeout = pCheckTimeout;
   }

   public ProfilerSystem(LongSupplier pTimeGetter, boolean pCheckTimeout) {
      this.startTime = pTimeGetter.getAsLong();
      this.timeGetter = pTimeGetter;
      this.startTick = this.tickSpan;
      this.endTickGetter = () -> this.tickSpan;
      this.checkTimeout = pCheckTimeout;
   }

   public void setTimeOutNanos(long timeOutNanos) {
      this.timeOutNanos = timeOutNanos;
   }

   public void startTick() {
      if (this.tickStarted) {
         LOGGER.error("Profiler tick already started - missing endTick()?");
      } else {
         this.tickStarted = true;
         this.fullPath = "";
         this.path.clear();
         this.tickSpan++;
         this.pathStack.clear();
         this.push("root");
      }
   }

   public void endTick() {
      if (!this.tickStarted) {
         LOGGER.error("Profiler tick already ended - missing startTick()?");
      } else {
         this.pop();
         this.tickStarted = false;
         if (!this.fullPath.isEmpty()) {
            LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", () -> ProfileResult.getHumanReadableName(this.fullPath));
         }

      }
   }

   public void push(String pP_18390_) {
      if (!this.tickStarted) {
         LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", pP_18390_);
      } else {
         if (!this.fullPath.isEmpty()) {
            this.fullPath = this.fullPath + "\u001e";
         }

         this.fullPath = this.fullPath + pP_18390_;
         this.path.add(this.fullPath);
         this.pathStack.push(pP_18390_);
         this.timeList.add(Utils.getMeasuringTimeNano());
         this.currentInfo = null;
      }
   }

   public void push(Supplier<String> pP_18392_) {
      this.push(pP_18392_.get());
   }

   public void markSampleType(SampleType pP_145928_) {
      this.sampleTypes.add(Pair.of(this.fullPath, pP_145928_));
   }

   public void pop() {
      if (!this.tickStarted) {
         LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
      } else if (this.timeList.isEmpty()) {
         LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
      } else {
         long i = Utils.getMeasuringTimeNano();
         long j = this.timeList.removeLong(this.timeList.size() - 1);
         this.path.remove(this.path.size() - 1);
         long k = i - j;
         LocatedInfo profilersystem$locatedinfo = this.getCurrentInfo();
         profilersystem$locatedinfo.totalTime += k;
         ++profilersystem$locatedinfo.visits;
         profilersystem$locatedinfo.maxTime = Math.max(profilersystem$locatedinfo.maxTime, k);
         profilersystem$locatedinfo.minTime = Math.min(profilersystem$locatedinfo.minTime, k);
         if (this.checkTimeout && k > timeOutNanos) {
            LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", () -> ProfileResult.getHumanReadableName(this.fullPath), () -> (double)k / 1000000.0D);
         }

         this.fullPath = this.path.isEmpty() ? "" : this.path.get(this.path.size() - 1);
         this.pathStack.pop();
         this.currentInfo = null;
      }
   }

   public void swap(String pP_18395_) {
      this.pop();
      this.push(pP_18395_);
   }

   public void swap(Supplier<String> pP_18397_) {
      this.pop();
      this.push(pP_18397_);
   }

   private LocatedInfo getCurrentInfo() {
      if (this.currentInfo == null) {
         this.currentInfo = this.locationInfos.computeIfAbsent(this.fullPath, (pP_18405_) -> new LocatedInfo());
      }

      return this.currentInfo;
   }

   public void visit(String pP_185247_, int pP_185248_) {
      this.getCurrentInfo().counts.addTo(pP_185247_, pP_185248_);
   }

   public void visit(Supplier<String> pP_185250_, int pP_185251_) {
      this.getCurrentInfo().counts.addTo(pP_185250_.get(), pP_185251_);
   }

   public ProfileResult getResult() {
      return new ProfileResultImpl(this.locationInfos, this.startTime, this.startTick, this.timeGetter.getAsLong(), this.endTickGetter.getAsInt());
   }

   @Nullable
   public LocatedInfo getInfo(String pP_145930_) {
      return this.locationInfos.get(pP_145930_);
   }

   public Set<Pair<String, SampleType>> getSampleTargets() {
      return this.sampleTypes;
   }

   public static class LocatedInfo implements ProfileLocationInfo {
      long maxTime = Long.MIN_VALUE;
      long minTime = Long.MAX_VALUE;
      long totalTime;
      long visits;
      final Object2LongOpenHashMap<String> counts = new Object2LongOpenHashMap<>();

      public long getTotalTime() {
         return this.totalTime;
      }

      public long getMaxTime() {
         return this.maxTime;
      }

      public long getVisitCount() {
         return this.visits;
      }

      public Object2LongMap<String> getCounts() {
         return Object2LongMaps.unmodifiable(this.counts);
      }
   }
}
