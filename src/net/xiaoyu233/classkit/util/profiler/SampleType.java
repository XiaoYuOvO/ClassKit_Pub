package net.xiaoyu233.classkit.util.profiler;

public enum SampleType {
   PATH_FINDING("pathfinding"),
   EVENT_LOOPS("event-loops"),
   MAIL_BOXES("mailboxes"),
   TICK_LOOP("ticking"),
   JVM("jvm"),
   CHUNK_RENDERING("chunk rendering"),
   CHUNK_RENDERING_DISPATCHING("chunk rendering dispatching"),
   CPU("cpu");

   private final String name;

   SampleType(String pP_145980_) {
      this.name = pP_145980_;
   }

   public String getName() {
      return this.name;
   }
}
