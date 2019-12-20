package com.ldsight.entity.xinjiangJson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ldgd on 2019/9/23.
 * 功能：
 * 说明：
 */

public class ProjectJson {


    /**
     * errno : 0
     * errmsg :
     * data : {"count":2,"totalPages":1,"pageSize":10,"currentPage":1,"data":[{"_id":40,"title":"中科洛丁展示项目/重庆展厅","lng":"106.541885","lat":"29.803683","smsphone":"","subgroups":"","admin":"ld"},{"_id":41,"title":"中科洛丁展示项目/深圳展厅","lng":"114.003727","lat":"22.635131","smsphone":"18033440703","subgroups":"[\"1\",\"2\"]","admin":"ld"}]}
     */

    private int errno;
    private String errmsg;
    private DataBeanX data;

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

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public static class DataBeanX {
        /**
         * count : 2
         * totalPages : 1
         * pageSize : 10
         * currentPage : 1
         * data : [{"_id":40,"title":"中科洛丁展示项目/重庆展厅","lng":"106.541885","lat":"29.803683","smsphone":"","subgroups":"","admin":"ld"},{"_id":41,"title":"中科洛丁展示项目/深圳展厅","lng":"114.003727","lat":"22.635131","smsphone":"18033440703","subgroups":"[\"1\",\"2\"]","admin":"ld"}]
         */

        private int count;
        private int totalPages;
        private int pageSize;
        private int currentPage;
        private List<ProjectInfo> data;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public List<ProjectInfo> getData() {
            return data;
        }

        public void setData(List<ProjectInfo> data) {
            this.data = data;
        }

        public static class ProjectInfo implements Serializable {
            /**
             * _id : 40
             * title : 中科洛丁展示项目/重庆展厅
             * lng : 106.541885
             * lat : 29.803683
             * smsphone :
             * subgroups :
             * admin : ld
             */

            private int _id;
            private String title;
            private String lng;
            private String lat;
            private String smsphone;
            private String subgroups;
            private String admin;

            public int get_id() {
                return _id;
            }

            public void set_id(int _id) {
                this._id = _id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getLng() {
                return lng;
            }

            public void setLng(String lng) {
                this.lng = lng;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getSmsphone() {
                return smsphone;
            }

            public void setSmsphone(String smsphone) {
                this.smsphone = smsphone;
            }

            public String getSubgroups() {
                return subgroups;
            }

            public void setSubgroups(String subgroups) {
                this.subgroups = subgroups;
            }

            public String getAdmin() {
                return admin;
            }

            public void setAdmin(String admin) {
                this.admin = admin;
            }

            @Override
            public String toString() {
                return "ProjectInfo{" +
                        "_id=" + _id +
                        ", title='" + title + '\'' +
                        ", lng='" + lng + '\'' +
                        ", lat='" + lat + '\'' +
                        ", smsphone='" + smsphone + '\'' +
                        ", subgroups='" + subgroups + '\'' +
                        ", admin='" + admin + '\'' +
                        '}';
            }
        }
    }
}
