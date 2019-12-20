package com.ldsight.entity.xinjiang;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ldgd on 2019/9/22.
 * 功能：
 * 说明：登录成功返回的json
 */

public class LoginJson implements Serializable {


    /**
     * errno : 0
     * errmsg :
     * data : {"token":{"username":"ld","token":"db7413a0-dd15-11e9-8c76-0b68964d4fc9","expired":1569746955738},"userProfile":{"_id":27,"username":"ld","phone":"","fullname":"洛丁","roles":""},"grantedActions":["device/view","project/view","v_device_ebox/view","v_device_lamp/view","v_device_lamp/control","v_device_lamp/edit"]}
     */

    private int errno;
    private String errmsg;
    private DataBean data;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * token : {"username":"ld","token":"db7413a0-dd15-11e9-8c76-0b68964d4fc9","expired":1569746955738}
         * userProfile : {"_id":27,"username":"ld","phone":"","fullname":"洛丁","roles":""}
         * grantedActions : ["device/view","project/view","v_device_ebox/view","v_device_lamp/view","v_device_lamp/control","v_device_lamp/edit"]
         */

        private TokenBean token;
        private UserProfileBean userProfile;
        private List<String> grantedActions;

        public TokenBean getToken() {
            return token;
        }

        public void setToken(TokenBean token) {
            this.token = token;
        }

        public UserProfileBean getUserProfile() {
            return userProfile;
        }

        public void setUserProfile(UserProfileBean userProfile) {
            this.userProfile = userProfile;
        }

        public List<String> getGrantedActions() {
            return grantedActions;
        }

        public void setGrantedActions(List<String> grantedActions) {
            this.grantedActions = grantedActions;
        }

        public static class TokenBean implements Serializable {
            /**
             * username : ld
             * token : db7413a0-dd15-11e9-8c76-0b68964d4fc9
             * expired : 1569746955738
             */

            private String username;
            private String token;
            private long expired;

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }

            public long getExpired() {
                return expired;
            }

            public void setExpired(long expired) {
                this.expired = expired;
            }
        }

        public static class UserProfileBean implements Serializable {
            /**
             * _id : 27
             * username : ld
             * phone :
             * fullname : 洛丁
             * roles :
             */

            private int _id;
            private String username;
            private String phone;
            private String fullname;
            private String roles;

            public int get_id() {
                return _id;
            }

            public void set_id(int _id) {
                this._id = _id;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getFullname() {
                return fullname;
            }

            public void setFullname(String fullname) {
                this.fullname = fullname;
            }

            public String getRoles() {
                return roles;
            }

            public void setRoles(String roles) {
                this.roles = roles;
            }
        }
    }
}
