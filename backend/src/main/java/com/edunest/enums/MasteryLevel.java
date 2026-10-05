package com.edunest.enums;

public enum MasteryLevel {
    WEAK,        // < 40%     -> Recommend Lesson + Practice + Revision
    NEEDS_WORK,  // 40% - 69% -> Recommend Practice + Revision
    STRONG,      // 70% - 89% -> Allow Progression + Revision
    MASTERED;    // >= 90%    -> Recommend Advanced Practical Challenge

    public static MasteryLevel fromScore(double score) {
        if (score < 40.0) return WEAK;
        if (score < 70.0) return NEEDS_WORK;
        if (score < 90.0) return STRONG;
        return MASTERED;
    }
}
