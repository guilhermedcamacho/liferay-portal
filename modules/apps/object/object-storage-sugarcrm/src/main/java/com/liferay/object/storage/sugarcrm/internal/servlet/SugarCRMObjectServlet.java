package com.liferay.object.storage.sugarcrm.internal.servlet;

import com.liferay.object.storage.sugarcrm.configuration.SugarCRMConfiguration;
import com.liferay.object.storage.sugarcrm.internal.constants.SugarCRMObjectConstants;
import com.liferay.object.storage.sugarcrm.internal.web.cache.SugarCRMAccessTokenWebCacheItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

@Component(
	immediate = true,
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.object.storage.sugarcrm.internal.servlet.SugarCRMObjectServlet",
		"osgi.http.whiteboard.servlet.pattern=/" + SugarCRMObjectConstants.SERVLET_PATH + "/*",
		"servlet.init.httpMethods=GET"
	},
	service = Servlet.class
)
public class SugarCRMObjectServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			User user = PortalUtil.getUser(httpServletRequest);

			if (null == user) {
				httpServletResponse.setStatus(
					HttpServletResponse.SC_UNAUTHORIZED);
				httpServletResponse.getWriter(
				).write(
					"401 Unauthorized - You are not authorized to access this resource."
				);

				return;
			}

			long companyId = PortalUtil.getDefaultCompanyId();

			SugarCRMConfiguration sugarcrmConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					SugarCRMConfiguration.class, companyId);

			JSONObject jsonObject = SugarCRMAccessTokenWebCacheItem.get(
				sugarcrmConfiguration);

			String access_token = jsonObject.getString("access_token");

			String externalURL = _getExternalURL(
				httpServletRequest, sugarcrmConfiguration);

			URL url = new URL(externalURL);

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)url.openConnection();

			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setRequestProperty(
				"Authorization", "Bearer " + access_token);

			try {
				httpServletResponse.setContentType(
					httpURLConnection.getContentType());

				InputStream inputStream = httpURLConnection.getInputStream();

				byte[] buffer = new byte[4096];
				int bytesRead;

				while ((bytesRead = inputStream.read(buffer)) != -1) {
					httpServletResponse.getOutputStream(
					).write(
						buffer, 0, bytesRead
					);
				}

				httpServletResponse.getOutputStream(
				).flush();
				httpServletResponse.getOutputStream(
				).close();
			}
			finally {
				httpURLConnection.disconnect();
			}
		}
		catch (Exception e) {
			_log.error("Error occured on doGet " + e.getMessage());
		}
	}

	private String _getExternalURL(
		HttpServletRequest httpServletRequest,
		SugarCRMConfiguration sugarCRMConfiguration) {

		String uri = httpServletRequest.getRequestURI();

		uri = uri.replace(
			httpServletRequest.getContextPath() + "/" +
				SugarCRMObjectConstants.SERVLET_PATH,
			"");

		String externalURL = sugarCRMConfiguration.baseURL() + uri;

		return externalURL;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SugarCRMObjectServlet.class);

}