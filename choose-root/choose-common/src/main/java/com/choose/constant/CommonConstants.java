package com.choose.constant;


import com.choose.user.pojos.UserInfo;

/**
 * @author 桌角的眼镜
 */
public interface CommonConstants {

    interface Common {
         int PANE_SIZE = 10;
         int PANE_MAX = 50;
    }

    interface LogLevel {
        String INFO = "INFO";
        String WARN = "WARN";
        String ERROR = "ERROR";
    }

    interface ChatFiend {
        Integer agree = 1;
        Integer ToBeConfirmed = 0;
        Integer Delete = -1;
    }

    interface ResultCode {
        interface SUCCESS {
            Integer code = 200;
            String message = "success";

        }

        interface NO_RESOURCES {
            Integer code = 204;
            String message = "no resource";

        }

        interface FORWARD {
            Integer code = 204;
            String message = "forward";

        }

        interface CLIENT_ERROR {
            Integer code = 400;
            String message = "client error";

        }

        interface NO_LOGIN {
            Integer code = 402;
            String message = "no login";

        }

        interface REFUSE {
            Integer code = 403;
            String message = "refuse";

        }

        interface BAN {
            Integer code = 405;
            String message = "ban";

        }

        interface ERROR {
            Integer code = 500;
            String message = "server error";

        }

        interface FLUSH {
            Integer code = 600;
            String message = "flush";

        }
    }

    interface File {
        String LOCAL = "local";
        String MIN_IO = "minio";
        String DEFAULT_BUCKET = "public";
        String RANDOM_NAME_PREFIX = "WeAI";

        interface MinIO {

            String SD_BUCKET = "stable-diffusion";
            String MJ_BUCKET = "mid-journey";
            String DE_BUCKET = "dall-e";

        }
    }

    interface Base64Prefix {
        String IMAGE_PNG = "data:image/png;base64,";
        String IMAGE_JPEG = "data:image/jpeg;base64,";
    }

    interface RedisKeyPrefix {
        String USER_TOKEN_CACHE_KEY = "user:token:";
        String ADMIN_TOKEN_CACHE_KEY = "admin:token:";
        String USER_INFO_TOKEN_CACHE_KEY = "user:info:";
        String ADMIN_INFO_TOKEN_CACHE_KEY = "user:info:";
        String RANGING_COLUMN_KEY = "range:column:";
        String TAG_KEY = "tag";
    }

    interface RPCBaseRespStatusCode {
        int SUCCESS = 200;
        int ERROR = 500;

        int REFRESH = 1001;
    }


    interface JWT {
        /**
         * 常量，表示一个 key 永不过期
         */
        Long NEVER_EXPIRE = -1L;
        /**
         * 常量，表示系统中不存在这个缓存
         */
        Long NOT_VALUE_EXPIRE = -2L;
        /**
         * 有效截止期 (时间戳)
         */
        String EFF = "eff";
        /**
         * 账号id
         */
        String LOGIN_ID = "loginId";
        /**
         * 乱数 （ 混入随机字符串，防止每次生成的 token 都是一样的 ）
         */
        String RN_STR = "rnStr";

        interface ExtraData {
            String userId = "userId";
            String userInfo = "userInfo";
            Class<UserInfo> userInfoType = UserInfo.class;
        }
    }

    interface JWTError {
        /**
         * 对 jwt 字符串解析失败
         */
        int CODE_30201 = 30201;

        /**
         * 此 jwt 的签名无效
         */
        int CODE_30202 = 30202;

        /**
         * 此 jwt 的 loginType 字段不符合预期
         */
        int CODE_30203 = 30203;

        /**
         * 此 jwt 已超时
         */
        int CODE_30204 = 30204;

        /**
         * 没有配置jwt秘钥
         */
        int CODE_30205 = 30205;

        /**
         * 登录时提供的账号id为空
         */
        int CODE_30206 = 30206;
    }

    interface DateFormat {
        String NORM_YEAR_PATTERN = "yyyy";
        String NORM_MONTH_PATTERN = "yyyy-MM";
        String SIMPLE_MONTH_PATTERN = "yyyyMM";
        String NORM_DATE_PATTERN = "yyyy-MM-dd";
        String NORM_TIME_PATTERN = "HH:mm:ss";
        String NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
        String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
        String NORM_DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
        String ISO8601_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";
        String CHINESE_DATE_PATTERN = "yyyy年MM月dd日";
        String CHINESE_DATE_TIME_PATTERN = "yyyy年MM月dd日HH时mm分ss秒";
        String PURE_DATE_PATTERN = "yyyyMMdd";
        String PURE_TIME_PATTERN = "HHmmss";
        String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";
        String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";
        String HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
        String JDK_DATETIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";
        String UTC_SIMPLE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
        String UTC_SIMPLE_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        String UTC_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
        String UTC_WITH_XXX_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
        String UTC_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String UTC_MS_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        String UTC_MS_WITH_XXX_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    }

    interface StrName {
        String NICKNAME = "御选";
    }
}
