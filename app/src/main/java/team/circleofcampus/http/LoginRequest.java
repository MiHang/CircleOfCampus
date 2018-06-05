package team.circleofcampus.http;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户登陆，注册，忘记密码部分的网络请求
 */
public class LoginRequest {

    /**
     * 忘记密码
     * @param email
     * @param pwd
     * @param verificationCode
     * @return
     */
    public static String resetPwd(String email, String pwd, String verificationCode) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("pwd", pwd);
            json.put("verificationCode", verificationCode);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/resetPassword.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取忘记密码的邮箱验证码
     * @param email
     * @return
     */
    public static String getForgotPwdVerificationCode(String email) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/getForgotPwdCode.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用户登陆
     * @param account - 账号
     * @param pwd - ID
     * @return
     */
    public static String login(String account, String pwd) {
        try {
            JSONObject json = new JSONObject();
            json.put("account", account);
            json.put("pwd", pwd);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/login.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 注册新用户
     * @param username - 用户名
     * @param gender - 性别
     * @param email - 邮箱
     * @param pwd - 密码
     * @param verificationCode - 验证码
     * @param facultyId - 院系ID
     * @return
     */
    public static String register(String username, String gender, String email,
                                  String pwd, String verificationCode, int facultyId) {
        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("gender", gender);
            json.put("email", email);
            json.put("pwd", pwd);
            json.put("verificationCode", verificationCode);
            json.put("facultyId", facultyId);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/register.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取院系信息
     * @param campusId
     * @return
     */
    public static String getFaculties (int campusId) {
        try {
            JSONObject json = new JSONObject();
            json.put("campusId", campusId);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/getFaculties.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取支持注册的所有学校
     * @return
     */
    public static String getCampuses() {
        return HttpRequest.postRequest(HttpRequest.URL + "coc/getCampuses.do", "");
    }

    /**
     * 判断用户名是否可用
     * @param username
     * @return
     */
    public static String getUsableName(String username) {
        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/isUsableName.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取注册邮箱验证码
     * @param email
     * @return
     */
    public static String getRegisterVerificationCode(String email) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/getRegisterCode.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
