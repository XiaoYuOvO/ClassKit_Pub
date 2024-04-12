package net.xiaoyu233.classkit.util.profiler;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import net.xiaoyu233.classkit.util.Utils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

public class ProfileResultImpl implements ProfileResult {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ProfileLocationInfo EMPTY_INFO = new ProfileLocationInfo() {
      public long getTotalTime() {
         return 0L;
      }

      public long getMaxTime() {
         return 0L;
      }

      public long getVisitCount() {
         return 0L;
      }

      public Object2LongMap<String> getCounts() {
         return Object2LongMaps.emptyMap();
      }
   };
   private static final Splitter SPLITTER = Splitter.on('\u001e');
   private static final Comparator<Entry<String, CounterInfo>> COMPARATOR = Entry.<String, CounterInfo>comparingByValue(Comparator.comparingLong((pP_18489_) -> pP_18489_.totalTime)).reversed();
   private final Map<String, ? extends ProfileLocationInfo> locationInfos;
   private final long startTime;
   private final int startTick;
   private final long endTime;
   private final int endTick;
   private final int tickDuration;

   public ProfileResultImpl(Map<String, ? extends ProfileLocationInfo> pLocationInfos, long pStartTime, int pStartTick, long pEndTime, int pEndTick) {
      this.locationInfos = pLocationInfos;
      this.startTime = pStartTime;
      this.startTick = pStartTick;
      this.endTime = pEndTime;
      this.endTick = pEndTick;
      this.tickDuration = pEndTick - pStartTick;
   }

   private ProfileLocationInfo getInfo(String pPath) {
      ProfileLocationInfo profilelocationinfo = this.locationInfos.get(pPath);
      return profilelocationinfo != null ? profilelocationinfo : EMPTY_INFO;
   }

   public List<ProfilerTiming> getTimings(String pP_18493_) {
      String s = pP_18493_;
      ProfileLocationInfo profilelocationinfo = this.getInfo("root");
      long i = profilelocationinfo.getTotalTime();
      ProfileLocationInfo profilelocationinfo1 = this.getInfo(pP_18493_);
      long j = profilelocationinfo1.getTotalTime();
      long k = profilelocationinfo1.getVisitCount();
      List<ProfilerTiming> list = Lists.newArrayList();
      if (!pP_18493_.isEmpty()) {
         pP_18493_ = pP_18493_ + "\u001e";
      }

      long l = 0L;

      for(String s1 : this.locationInfos.keySet()) {
         if (isSubpath(pP_18493_, s1)) {
            l += this.getInfo(s1).getTotalTime();
         }
      }

      float f = (float)l;
      if (l < j) {
         l = j;
      }

      if (i < l) {
         i = l;
      }

      for(String s2 : this.locationInfos.keySet()) {
         if (isSubpath(pP_18493_, s2)) {
            ProfileLocationInfo profilelocationinfo2 = this.getInfo(s2);
            long i1 = profilelocationinfo2.getTotalTime();
            double d0 = (double)i1 * 100.0D / (double)l;
            double d1 = (double)i1 * 100.0D / (double)i;
            String s3 = s2.substring(pP_18493_.length());
            list.add(new ProfilerTiming(s3, d0, d1, profilelocationinfo2.getVisitCount()));
         }
      }

      if ((float)l > f) {
         list.add(new ProfilerTiming("unspecified", (double)((float)l - f) * 100.0D / (double)l, (double)((float)l - f) * 100.0D / (double)i, k));
      }

      Collections.sort(list);
      list.add(0, new ProfilerTiming(s, 100.0D, (double)l * 100.0D / (double)i, k));
      return list;
   }

   private static boolean isSubpath(String pParent, String pPath) {
      return pPath.length() > pParent.length() && pPath.startsWith(pParent) && pPath.indexOf(30, pParent.length() + 1) < 0;
   }

   private Map<String, CounterInfo> setupCounters() {
      Map<String, CounterInfo> map = Maps.newTreeMap();
      this.locationInfos.forEach((pP_18512_, pP_18513_) -> {
         Object2LongMap<String> object2longmap = pP_18513_.getCounts();
         if (!object2longmap.isEmpty()) {
            List<String> list = SPLITTER.splitToList(pP_18512_);
            object2longmap.forEach((pP_145944_, pP_145945_) -> map.computeIfAbsent(pP_145944_, (pP_145947_) -> new CounterInfo()).add(list.iterator(), pP_145945_));
         }

      });
      return map;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public int getStartTick() {
      return this.startTick;
   }

   public long getEndTime() {
      return this.endTime;
   }

   public int getEndTick() {
      return this.endTick;
   }

   public boolean save(Path pP_145940_) {
      Writer writer = null;

      boolean flag;
      try {
         Files.createDirectories(pP_145940_.getParent());
         writer = Files.newBufferedWriter(pP_145940_, StandardCharsets.UTF_8);
         writer.write(this.asString(this.getTimeSpan(), this.getTickSpan()));
         return true;
      } catch (Throwable throwable) {
         LOGGER.error("Could not save profiler results to {}", pP_145940_, throwable);
         flag = false;
      } finally {
         IOUtils.closeQuietly(writer);
      }

      return flag;
   }

   protected String asString(long pTimeSpan, int pTickSpan) {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("---- Profiler Results ----\n");
      stringbuilder.append("\n\n");
//      stringbuilder.append("Version: ").append(SharedConstants.getGameVersion().getId()).append('\n');
      stringbuilder.append("Time span: ").append(pTimeSpan / 1000000L).append(" ms\n");
      stringbuilder.append("Tick span: ").append(pTickSpan).append(" ticks\n");
      stringbuilder.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", (float)pTickSpan / ((float)pTimeSpan / 1.0E9F))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
      stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.appendTiming(0, "root", stringbuilder);
      stringbuilder.append("--- END PROFILE DUMP ---\n\n");
      Map<String, CounterInfo> map = this.setupCounters();
      if (!map.isEmpty()) {
         stringbuilder.append("--- BEGIN COUNTER DUMP ---\n\n");
         this.appendCounterDump(map, stringbuilder, pTickSpan);
         stringbuilder.append("--- END COUNTER DUMP ---\n\n");
      }

      return stringbuilder.toString();
   }

   public String getRootTimings() {
      StringBuilder stringbuilder = new StringBuilder();
      this.appendTiming(0, "root", stringbuilder);
      return stringbuilder.toString();
   }

   private static StringBuilder indent(StringBuilder pSb, int pSize) {
      pSb.append(String.format("[%02d] ", pSize));

      pSb.append(Strings.repeat("|   ",(Math.max(0, pSize))));

      return pSb;
   }

   private void appendTiming(int pLevel, String pName, StringBuilder pSb) {
      List<ProfilerTiming> list = this.getTimings(pName);
      Object2LongMap<String> object2longmap = ObjectUtils.firstNonNull(this.locationInfos.get(pName), EMPTY_INFO).getCounts();
      object2longmap.forEach((pP_18508_, pP_18509_) -> indent(pSb, pLevel).append('#').append(pP_18508_).append(' ').append(pP_18509_).append('/').append(pP_18509_ / (long)this.tickDuration).append('\n'));
      if (list.size() >= 3) {
         for(int i = 1; i < list.size(); ++i) {
            ProfilerTiming profilertiming = list.get(i);
            indent(pSb, pLevel).append(profilertiming.name).append('(').append(profilertiming.visitCount).append('/').append(String.format(Locale.ROOT, "%.0f", (float)profilertiming.visitCount / (float)this.tickDuration)).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", profilertiming.parentSectionUsagePercentage)).append("%/").append(String.format(Locale.ROOT, "%.2f", profilertiming.totalUsagePercentage)).append("%\n");
            if (!"unspecified".equals(profilertiming.name)) {
               try {
                  this.appendTiming(pLevel + 1, pName + "\u001e" + profilertiming.name, pSb);
               } catch (Exception exception) {
                  pSb.append("[[ EXCEPTION ").append(exception).append(" ]]");
               }
            }
         }

      }
   }

   private void appendCounter(int pDepth, String pName, CounterInfo pInfo, int pTickSpan, StringBuilder pSb) {
      indent(pSb, pDepth).append(pName).append(" total:").append(pInfo.selfTime).append('/').append(pInfo.totalTime).append(" average: ").append(pInfo.selfTime / (long)pTickSpan).append('/').append(pInfo.totalTime / (long)pTickSpan).append('\n');
      pInfo.subCounters.entrySet().stream().sorted(COMPARATOR).forEach((pP_18474_) -> this.appendCounter(pDepth + 1, pP_18474_.getKey(), pP_18474_.getValue(), pTickSpan, pSb));
   }

   private void appendCounterDump(Map<String, CounterInfo> pCounters, StringBuilder pSb, int pTickSpan) {
      pCounters.forEach((pP_18503_, pP_18504_) -> {
         pSb.append("-- Counter: ").append(pP_18503_).append(" --\n");
         this.appendCounter(0, "root", pP_18504_.subCounters.get("root"), pTickSpan, pSb);
         pSb.append("\n\n");
      });
   }

   public int getTickSpan() {
      return this.tickDuration;
   }

   static class CounterInfo {
      long selfTime;
      long totalTime;
      final Map<String, CounterInfo> subCounters = Maps.newHashMap();

      public void add(Iterator<String> pPathIterator, long pTime) {
         this.totalTime += pTime;
         if (!pPathIterator.hasNext()) {
            this.selfTime += pTime;
         } else {
            this.subCounters.computeIfAbsent(pPathIterator.next(), (pP_18546_) -> new CounterInfo()).add(pPathIterator, pTime);
         }

      }
   }
}
