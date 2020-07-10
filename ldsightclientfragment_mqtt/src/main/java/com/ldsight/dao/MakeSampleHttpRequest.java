package com.ldsight.dao;

import android.app.Activity;
import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ldsightclient_jgd.R;
import com.ldsight.adapter.AlertAdapter;
import com.ldsight.entity.AlertDevice;
import com.ldsight.entity.CheckUser;
import com.ldsight.entity.StreetAndDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;*/

/*
 * 获取路灯信息
 */
public class MakeSampleHttpRequest {
    private Activity mActivity;
    private JsonObjectRequest jsonObjRequest;
    private RequestQueue mVolleyQueue;
    private final String TAG_REQUEST = "MY_TAG";
    private String[] streetNames;
    private ArrayList<StreetAndDevice> streetAndDevices;
    private JSONObject mJSONObject = null;

    public MakeSampleHttpRequest(Activity pActivity) {
        // TODO Auto-generated constructor stub
        mActivity = pActivity;
        mVolleyQueue = Volley.newRequestQueue(pActivity);
        streetAndDevices = new ArrayList<StreetAndDevice>();

    }

    /**
     * 获取所有数据Names信息
     *
     * @return
     */
    public String[] makeSampleHttpRequest() {
        String ip = mActivity.getString(R.string.ip);
        String url = "http://" + ip + ":8080/ldsight/deviceAction";

        jsonObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("streetAndDevices")) {
                                try {
                                    JSONArray jsonArr = response
                                            .getJSONArray("streetAndDevices");
                                    streetAndDevices.clear();

                                    for (int i = 0; i < jsonArr.length(); i++) {

                                        JSONObject temp = jsonArr
                                                .getJSONObject(i);

                                        if (temp.isNull("deviceParam")) {
                                            continue;
                                        }

                                        JSONObject deviceParam = temp
                                                .getJSONObject("deviceParam");
                                        JSONObject street = temp
                                                .getJSONObject("street");
                                        StreetAndDevice streetAndDevice = new StreetAndDevice();
                                        streetAndDevice.setCityId(street
                                                .getString("cityId"));
                                        streetAndDevice.setEndTime(street
                                                .getString("endTime"));
                                        streetAndDevice.setStartTime(street
                                                .getString("startTime"));
                                        streetAndDevice.setEnergy100(street
                                                .getString("energy100"));
                                        streetAndDevice.setEnergy75(street
                                                .getString("energy75"));
                                        streetAndDevice.setEnergy50(street
                                                .getString("energy50"));
                                        streetAndDevice.setEnergy25(street
                                                .getString("energy25"));
                                        streetAndDevice.setDeviceId(street
                                                .getString("deviceId"));

                                        streetAndDevice.setDeviceParamId(deviceParam
                                                .getInt("deviceParamId"));
                                        streetAndDevice
                                                .setMb_a_Ampere(deviceParam
                                                        .getInt("mb_a_Ampere"));
                                        streetAndDevice
                                                .setMb_a_volt(deviceParam
                                                        .getInt("mb_a_volt"));
                                        streetAndDevice.setMb_addr(deviceParam
                                                .getInt("mb_addr"));
                                        streetAndDevice
                                                .setMb_b_Ampere(deviceParam
                                                        .getInt("mb_b_Ampere"));
                                        streetAndDevice
                                                .setMb_b_volt(deviceParam
                                                        .getInt("mb_b_volt"));
                                        streetAndDevice
                                                .setMb_c_Ampere(deviceParam
                                                        .getInt("mb_c_Ampere"));
                                        streetAndDevice
                                                .setMb_c_volt(deviceParam
                                                        .getInt("mb_c_volt"));
                                        streetAndDevice.setMb_func(deviceParam
                                                .getInt("mb_func"));
                                        streetAndDevice.setMb_hz(deviceParam
                                                .getInt("mb_hz"));
                                        streetAndDevice.setMb_ned(deviceParam
                                                .getInt("mb_ned"));
                                        streetAndDevice.setMb_pa(deviceParam
                                                .getInt("mb_pa"));
                                        streetAndDevice.setMb_pb(deviceParam
                                                .getInt("mb_pb"));
                                        streetAndDevice.setMb_pc(deviceParam
                                                .getInt("mb_pc"));
                                        streetAndDevice.setMb_pfav(deviceParam
                                                .getInt("mb_pfav"));
                                        streetAndDevice.setMb_psum(deviceParam
                                                .getInt("mb_psum"));
                                        streetAndDevice.setMb_qa(deviceParam
                                                .getInt("mb_qa"));
                                        streetAndDevice.setMb_qb(deviceParam
                                                .getInt("mb_qb"));
                                        streetAndDevice.setMb_qc(deviceParam
                                                .getInt("mb_qc"));
                                        streetAndDevice.setMb_qsum(deviceParam
                                                .getInt("mb_qsum"));
                                        streetAndDevice.setMb_size(deviceParam
                                                .getInt("mb_size"));
                                        streetAndDevice.setMb_ssum(deviceParam
                                                .getInt("mb_ssum"));
                                        streetAndDevice.setMb_time(deviceParam
                                                .getString("mb_time"));

                                        streetAndDevice.setMb_yed(deviceParam
                                                .getInt("mb_yed"));
                                        streetAndDevice.setStreetId(street
                                                .getString("streetId"));
                                        streetAndDevice.setStreetName(street
                                                .getString("streetName"));
                                        streetAndDevice.setUuid(street
                                                .getString("uuid"));

                                        // 测试只显示东西路灯
                                        /*
										 * if (streetAndDevice.getStreetId()
										 * .equals("SZ1001") || streetAndDevice
										 * .getStreetId().equals( "SZ1002")) {
										 * streetAndDevices
										 * .add(streetAndDevice); }
										 */
                                        // 根据用户显示不同的数据
                                        CheckUser cku = CheckUser.getInstance();
                                        String streetId = street
                                                .getString("streetId");
                                        if (cku.getUserName().equals("zky")) {
                                            if (!streetId.equals("SZ1018")
                                                    && !streetId
                                                    .equals("SZ1019")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "mys")) {
                                            if (!streetId.equals("SZ1023")
                                                    && !streetId
                                                    .equals("SZ1024")
                                                    && !streetId
                                                    .equals("SZ1025")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "csazf")) {
                                            if (!streetId.equals("SZ1061")
                                                    && !streetId
                                                    .equals("SZ1062")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "ysdx")) {
                                            if (!streetId.equals("SZ1012")
                                                    && !streetId
                                                    .equals("SZ1013")
                                                    && !streetId
                                                    .equals("SZ1014")
                                                    && !streetId
                                                    .equals("SZ1015")
                                                    && !streetId
                                                    .equals("SZ1016")
                                                    && !streetId
                                                    .equals("SZ1017")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "zky2")) {
                                            if (!streetId.equals("SZ1018")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "zst")) {
                                            if (!streetId.equals("SZ1019")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "admin")) {
                                            if (!streetId.equals("SZ1001")
                                                    && !streetId
                                                    .equals("SZ1002")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "ldgd")) {
                                            if (!streetId.equals("SZ1010")
                                                    && !streetId
                                                    .equals("SZ1003")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "ynyl")) {
                                            if (!streetId.equals("SZ1032")
                                                    && !streetId
                                                    .equals("SZ1033")
                                                    && !streetId
                                                    .equals("SZ1034")
                                                    && !streetId
                                                    .equals("SZ1035")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "sxtc")) {
                                            if (!streetId.equals("SZ1036")
                                                    && !streetId
                                                    .equals("SZ1037")
                                                    && !streetId
                                                    .equals("SZ1038")) {
                                                continue;
                                            }
                                        } else if (cku.getUserName().equals(
                                                "zj312")) {
                                            if (!streetId.equals("SZ1043")
                                                    && !streetId
                                                    .equals("SZ1044")
                                                    && !streetId
                                                    .equals("SZ1045")
                                                    && !streetId
                                                    .equals("SZ1046")
                                                    && !streetId
                                                    .equals("SZ1047")
                                                    && !streetId
                                                    .equals("SZ1048")
                                                    && !streetId
                                                    .equals("SZ1049")
                                                    && !streetId
                                                    .equals("SZ1050")
                                                    && !streetId
                                                    .equals("SZ1051")
                                                    && !streetId
                                                    .equals("SZ1052")
                                                    && !streetId.equals("SZ1053") && !streetId.equals("SZ1054")
                                                    && !streetId.equals("SZ1056")
                                                    && !streetId.equals("SZ1057")
                                                    && !streetId.equals("SZ1058")) {
                                                continue;
                                            }
                                        } else {
                                            return;
                                        }

                                        streetAndDevices.add(streetAndDevice);

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                } else if (error instanceof ClientError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                } else if (error instanceof TimeoutError) {
                }

            }
        });

        // Set a retry policy in case of SocketTimeout & ConnectionTimeout
        // Exceptions. Volley does retry for you if you have specified the
        // policy.
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjRequest.setTag(TAG_REQUEST);
        mVolleyQueue.add(jsonObjRequest);
        // mVolleyQueue.start();

        return streetNames;
    }

    /**
     * 服务器获取版本号和版本名(volley)
     *
     * @return
     */
    public JSONObject getVersion() {
        // http://121.40.194.91:8080/Androidnew/Androidnew
        System.out.println("执行");
        String ip = mActivity.getString(R.string.ip);
        String url = "http://" + ip + ":8080/Androidnew/Androidnew";
        jsonObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // {"verCode":"3.0","verName":"2.1.2"}
                        try {
                            mJSONObject = response;
                            int versionCode = response.getInt("verCode");
                            String versionName = response.getString("verName");
                            System.out.println("xx = " + versionCode + "\n"
                                    + versionName);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                } else if (error instanceof ClientError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                } else if (error instanceof TimeoutError) {
                }

            }
        });

        // Set a retry policy in case of SocketTimeout & ConnectionTimeout
        // Exceptions. Volley does retry for you if you have specified the
        // policy.
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjRequest.setTag(TAG_REQUEST);
        mVolleyQueue.add(jsonObjRequest);
        // mVolleyQueue.start();

        return mJSONObject;
    }

    /**
     * 服务器获取版本号和版本名(HttpPost)
     *
     * @return
     */
   public JSONObject post_to_server() {
        // String ip = mActivity.getString(R.string.ip);
        // String url = "http://" + ip + ":8080/Androidnew/Androidnew";

		/*String url = "http://121.40.194.91:8089/APP/getUpdate";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpResponse response = null;
			// 创建httpost.访问本地服务器网址
			HttpGet httpost = new HttpGet(url);
			StringBuilder builder = new StringBuilder();

			// httpost.setEntity(new UrlEncodedFormEntity(vps, HTTP.UTF_8));
			response = httpclient.execute(httpost); // 执行

			if (response.getEntity() != null) {
				// 如果服务器端JSON没写对，这句是会出异常，是执行不过去的
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				String s = reader.readLine();
				for (; s != null; s = reader.readLine()) {
					builder.append(s);
				}
			}
			JSONObject mJSONObject = new JSONObject(builder.toString());
			Log.e("xxxx","mJSONObject = " + mJSONObject.toString());
			return mJSONObject;

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("msg", e.getMessage());
			return null;
		} finally {
			try {
				httpclient.getConnectionManager().shutdown();// 关闭连接
				// 这两种释放连接的方法都可以
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("msg", e.getMessage());
			}
		}*/

		return null;


    }

    /**
     * 更具设备ID获取当前电箱的报警记录
     */
    public List<AlertDevice> getalarmRecor(String deviceId, final AlertAdapter alertAdapter, final List<AlertDevice> alertDevices) {

        String ip = mActivity.getString(R.string.ip);
        String url = "http://" + ip + ":8080/alarm/AlarmJsons";
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("Device", deviceId);


        jsonObjRequest = new JsonObjectRequest(Request.Method.GET, builder.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.has("td")) {
                                try {
                                    JSONArray jsonArr = response.getJSONArray("td");
                                    alertDevices.clear();
                                    for (int i = 0; i < jsonArr.length(); i++) {
                                        JSONObject temp = jsonArr.getJSONObject(i);
                                        AlertDevice ad = new AlertDevice();

                                        ad.setAlarmId(temp.getInt("alarm_id"));
                                        ad.setAlarmType(temp.getInt("alarm_type"));
                                        ad.setAlarmAmpere(temp.getInt("ampere"));
                                        ad.setAlarmDeviceAddress(temp.getInt("device_address"));
                                        ad.setAlarmDeviceId(temp.getString("device_id"));
                                        ad.setAlarmElectric(temp.getInt("electric"));
                                        ad.setAlarmFactor(temp.getInt("factor"));
                                        ad.setAlarmPower(temp.getInt("power"));
                                        ad.setAlarmVoltage(temp.getInt("voltage"));
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        ad.setDate(temp.getString("date"));
                                        //	Date date = df.parse(temp.getString("date"));
                                        alertDevices.add(ad);
                                    }
                                    // 测试
								/*	System.out.println("报警id" + alertDevices.get(1).getAlarmId());
									System.out.println("电流" + alertDevices.get(1).getAlarmAmpere());
									System.out.println("设备地址" + alertDevices.get(1).getAlarmDeviceAddress());
									System.out.println("电箱id" + alertDevices.get(1).getAlarmDeviceId());
									System.out.println("电能" + alertDevices.get(1).getAlarmElectric());
									System.out.println("功率因数" + alertDevices.get(1).getAlarmFactor());
									System.out.println("功率" + alertDevices.get(1).getAlarmPower());
									System.out.println("电压" + alertDevices.get(1).getAlarmVoltage());*/
                                    alertAdapter.notifyDataSetChanged();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                } else if (error instanceof ClientError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                } else if (error instanceof TimeoutError) {
                }

                // showToast(error.getMessage());
            }
        });

        // Set a retry policy in case of SocketTimeout & ConnectionTimeout
        // Exceptions. Volley does retry for you if you have specified the
        // policy.
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjRequest.setTag(TAG_REQUEST);
        mVolleyQueue.add(jsonObjRequest);
        // mVolleyQueue.start();

        System.out.println(alertDevices.size() + "returnaa");
        return alertDevices;

    }


}
