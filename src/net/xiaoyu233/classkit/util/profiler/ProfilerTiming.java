package net.xiaoyu233.classkit.util.profiler;

public final class ProfilerTiming implements Comparable<ProfilerTiming> {
   public final double parentSectionUsagePercentage;
   public final double totalUsagePercentage;
   public final long visitCount;
   public final String name;

   public ProfilerTiming(String pName, double pParentUsagePercentage, double pTotalUsagePercentage, long pVisitCount) {
      this.name = pName;
      this.parentSectionUsagePercentage = pParentUsagePercentage;
      this.totalUsagePercentage = pTotalUsagePercentage;
      this.visitCount = pVisitCount;
   }

   public int compareTo(ProfilerTiming pP_18618_) {
      if (pP_18618_.parentSectionUsagePercentage < this.parentSectionUsagePercentage) {
         return -1;
      } else {
         return pP_18618_.parentSectionUsagePercentage > this.parentSectionUsagePercentage ? 1 : pP_18618_.name.compareTo(this.name);
      }
   }

   public int getColor() {
      return (this.name.hashCode() & 11184810) + 4473924;
   }
}
