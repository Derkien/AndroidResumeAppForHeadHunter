package com.example.resumeviewer;

import android.provider.BaseColumns;

public final class FeedTablesContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public FeedTablesContract() {}

    /* Resume table  */
    public static abstract class TableUserResume implements BaseColumns {
        public static final String TABLE_NAME = "user_resume";
//        public static final String COLUMN_NAME_RESUME_ID = "resumeid";
        public static final String COLUMN_NAME_USER_NAME = "name";
//        public static final String COLUMN_NAME_USER_UNIQUE = "uniqueuser";
//        public static final String COLUMN_NAME_USER_SNAME = "sname";
//        public static final String COLUMN_NAME_USER_PNAME = "pname";
        public static final String COLUMN_NAME_USER_DATE_BIRTH = "datebirth";
        public static final String COLUMN_NAME_USER_SEX = "sex";
        public static final String COLUMN_NAME_USER_JOB = "job";
        public static final String COLUMN_NAME_USER_SALARY = "salary";
        public static final String COLUMN_NAME_USER_PHONE = "phone";
        public static final String COLUMN_NAME_USER_EMAIL = "email";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_SENT = "sent";
    }
    
    /* Answers table  */
    public static abstract class TableResumeAnsw implements BaseColumns {
        public static final String TABLE_NAME = "resume_answers";
//        public static final String COLUMN_NAME_ANSW_ID = "answerid";
        public static final String COLUMN_RESUME_ID = "resumeid";
        public static final String COLUMN_NAME_ANSW_TEXT = "text";
//        public static final String COLUMN_NAME_ANSW_FROM = "email";
        public static final String COLUMN_NAME_ANSW_FROMORG = "employerid";
        public static final String COLUMN_NAME_ANSW_CHECKED = "checked";
        public static final String COLUMN_NAME_ANSW_TIME = "time";
    }
    
    //another...
    /* Maybe another emploery table...  */
//    public static abstract class TableEmployer implements BaseColumns {
//        public static final String TABLE_NAME = "employer";
//        public static final String COLUMN_NAME_ANSW_ID = "answerid";
//        public static final String COLUMN_RESUME_ID = "resumeid";
//        public static final String COLUMN_ORG_NAME = "organization";
//        public static final String COLUMN_PENDING = "pending";
//    }
    
}