package com.getsocial.events.Utils;

/**
 * Created by pavan.pc on 09/06/16.
 */
public class Constants {
        public static final String IDENTITY_HEADER = "X-Auth-MSISDN";

        public static final String CONTENT_TYPE_JSON = "application/json";

        public static final String ERROR500 = "Unexpected Internal server error occured";
        public static final String ERROR400 = "Event Type / Start time / End time is missing /invalid. please check input parameters again";
        public static final String ERROR600 = "Start time must be less than end time. Please check input params";
        public static final String ERROR422 = "Invalid Input Parameter(s)";

        public static int HTTP_INTERNAL_SERVER_ERROR = 500;
        public static int HTTP_SUCCESS = 200;
        public static int HTTP_BAD_REQUEST = 400;
        public static int HTTP_UNPROCESSABLE_ENTITY = 422;

    }

