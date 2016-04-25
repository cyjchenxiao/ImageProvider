package com.qiyi.imageprovider.util;

import java.util.Arrays;
import java.util.Comparator;

public class CornerSpecUtils {
    private CornerSpecUtils() { }
    
    public enum Corner {
        TOP_LEFT("tl"), TOP_RIGHT("tr"), BOTTOM_LEFT("bl"), BOTTOM_RIGHT("br");
        
        private String mDescription;
        private Corner(String description) {
            mDescription = description;
        }
        
        public String getDescription() {
            return mDescription;
        }
    }
    
    public static class CornerSpec {
        private Corner mCorner;
        private float mRadius;
        
        public CornerSpec(Corner corner, float radius) {
            mCorner = corner;
            mRadius = radius;
        }
        
        public Corner getCorner() {
            return mCorner;
        }
        
        public float getRadius() {
            return mRadius;
        }
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("CornerSpec{");
            builder.append(mCorner);
            builder.append(", ").append(mRadius);
            builder.append("}");
            return builder.toString();
        }
    }
    
    public static void sortAsc(CornerSpec[] specs) {
        Arrays.sort(specs, new Comparator<CornerSpec>() {
            @Override
            public int compare(CornerSpec lhs, CornerSpec rhs) {
                return lhs.getCorner().ordinal() - rhs.getCorner().ordinal();
            }
        });
    }
}
