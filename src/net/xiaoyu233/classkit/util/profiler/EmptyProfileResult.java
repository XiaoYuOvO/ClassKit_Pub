package net.xiaoyu233.classkit.util.profiler;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class EmptyProfileResult implements ProfileResult {
   public static final EmptyProfileResult INSTANCE = new EmptyProfileResult();

   private EmptyProfileResult() {
   }

   public List<ProfilerTiming> getTimings(String pP_18448_) {
      return Collections.emptyList();
   }

   public boolean save(Path pP_145937_) {
      return false;
   }

   public long getStartTime() {
      return 0L;
   }

   public int getStartTick() {
      return 0;
   }

   public long getEndTime() {
      return 0L;
   }

   public int getEndTick() {
      return 0;
   }

   public String getRootTimings() {
      return "";
   }
}
