package com.dashuai.learning.shiro.filter;

import com.dashuai.learning.shiro.model.UserInfo;
import com.dashuai.learning.utils.json.JSONParseUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 思路：
 * 1.读取当前登录用户名，获取在缓存中的sessionId队列
 * 2.判断队列的长度，大于最大登录限制的时候，按踢出规则
 * 将之前的sessionId中的session域中存入kickout：true，并更新队列缓存
 * 3.判断当前登录的session域中的kickout如果为true，
 * 想将其做退出登录处理，然后再重定向到踢出登录提示页面
 *
 * @author Liaozihong
 */
public class KickoutSessionControlFilter extends AccessControlFilter {
    /**
     * 踢出后到的地址
     */
    private String kickoutUrl;
    /**
     * 踢出之前登录的/之后登录的用户 默认踢出之前登录的用户
     */
    private boolean kickoutAfter = false;
    /**
     * 运行同一账号同时登录数
     */
    private int maxSession = 1;
    private SessionManager sessionManager;
    private Cache<String, Deque<Serializable>> cache;

    /**
     * Sets kickout url.
     *
     * @param kickoutUrl the kickout url
     */
    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }

    /**
     * Sets kickout after.
     *
     * @param kickoutAfter the kickout after
     */
    public void setKickoutAfter(boolean kickoutAfter) {
        this.kickoutAfter = kickoutAfter;
    }

    /**
     * Sets max session.
     *
     * @param maxSession the max session
     */
    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }

    /**
     * Sets session manager.
     *
     * @param sessionManager the session manager
     */
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Sets cache manager.
     * 设置Cache的key的前缀
     *
     * @param cacheManager the cache manager
     */
    public void setCacheManager(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("shiro_redis_cache");
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            //如果没有登录，直接进行之后的流程
            return true;
        }
        Session session = subject.getSession();
        UserInfo user = (UserInfo) subject.getPrincipal();
        String username = user.getUsername();
        Serializable sessionId = session.getId();
        //读取redis缓存
        Deque<Serializable> deque = cache.get(username);
        //如果没有改集合队列，则创建
        if (deque == null) {
            deque = new LinkedList<>();
            //将sessionId存入队列
            deque.push(sessionId);
            //将用户的sessionId队列缓存进redis，以username为key标识
            cache.put(username, deque);
        }
        //设置用户默认没被踢
        session.setAttribute("kickout", true);

        //如果队列里没有此sessionId，且用户没有被踢出；放入队列
        if (!deque.contains(sessionId)) {
            deque.push(sessionId);
            cache.put(username, deque);
        }
        //如果队列里的sessionId数超出最大会话数，开始踢人
        while (deque.size() > maxSession) {
            Serializable kickoutSessionId = null;
            //如果踢出后者
            if (kickoutAfter) {
                kickoutSessionId = deque.removeFirst();
            } else { //否则踢出前者
                kickoutSessionId = deque.removeLast();
            }
            //踢出后再更新下缓存队列
            cache.put(username, deque);
            try {
                //获取被踢出的sessionId的session对象
                Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
                if (kickoutSession != null) {
                    //设置会话的kickout属性表示踢出了
                    session.setAttribute("kickout", false);
                }
            } catch (Exception e) {//ignore exception
            }
        }
        //如果被踢出了，直接退出，重定向到踢出后的地址
        if (!((Boolean) session.getAttribute("kickout"))) {
            try {
                //退出登录
                subject.logout();
            } catch (Exception e) { //ignore
            }
//            saveRequest(request);
            //判断是不是Ajax请求
            if ("XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest) request).getHeader("X-Requested-With"))) {
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("user_status", "300");
                resultMap.put("message", "您已经在其他地方登录，请重新登录！");
                //输出json串
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println(JSONParseUtils.object2JsonString(resultMap));
                out.flush();
                out.close();
                return false;
            } else {
                //重定向
                WebUtils.issueRedirect(request, response, kickoutUrl);
                return false;
            }
        }
        return true;
    }
}