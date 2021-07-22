<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/info_list_basic_list/init.jsp" %>

<c:choose>
	<c:when test="<%= Objects.equals(infoListStyleKey, BasicListInfoListStyle.NUMBERED.getKey()) %>">
		<ol>

			<%
			for (Object infoListObject : infoListObjects) {
			%>

				<li>
					<c:choose>
						<c:when test="<%= (infoItemRenderer instanceof InfoItemTemplatedRenderer) && Validator.isNotNull(templateKey) %>">
							<b>passou 1</b>
							<%
							InfoItemTemplatedRenderer<Object> infoItemTemplatedRenderer = (InfoItemTemplatedRenderer)infoItemRenderer;

							infoItemTemplatedRenderer.render(infoListObject, templateKey, request, PipingServletResponseFactory.createPipingServletResponse(pageContext));
							%>

						</c:when>
						<c:otherwise>
							<b>passou 2</b>
							<%
							infoItemRenderer.render(infoListObject, request, PipingServletResponseFactory.createPipingServletResponse(pageContext));
							%>

						</c:otherwise>
					</c:choose>
				</li>

			<%
			}
			%>

		</ol>
	</c:when>
	<c:otherwise>
		<div class="table-responsive">
			<table class="table table-autofit">
				<thead>
					<tr>
						<%
							for (String objectColumnName : infoListObjectColumns) {
						%>
							<th class="table-cell-expand-smallest">
								<liferay-ui:message key="<%= objectColumnName %>" />
							</th>
						<%
							}
						%>
					</tr>
				</thead>
				<tbody>
					<%
						for (Object infoListObject : infoListObjects) {
					%>
						<tr class=table-active">
							<%
								infoItemRenderer.render(infoListObject, request, PipingServletResponseFactory.createPipingServletResponse(pageContext));
							%>
						</tr>
					<%
						}
					%>
				</tbody>
			</table>
		</div>

<%--		<ul class="<%= listCssClass %>">--%>

<%--			<%--%>
<%--			for (Object infoListObject : infoListObjects) {--%>
<%--			%>--%>

<%--				<li class="<%= listItemCssClass %>">--%>
<%--					<c:choose>--%>
<%--						<c:when test="<%= (infoItemRenderer instanceof InfoItemTemplatedRenderer) && Validator.isNotNull(templateKey) %>">--%>
<%--							<b>passou 3</b>--%>
<%--							<%--%>
<%--							InfoItemTemplatedRenderer<Object> infoItemTemplatedRenderer = (InfoItemTemplatedRenderer)infoItemRenderer;--%>

<%--							infoItemTemplatedRenderer.render(infoListObject, templateKey, request, PipingServletResponseFactory.createPipingServletResponse(pageContext));--%>
<%--							%>--%>

<%--						</c:when>--%>
<%--						<c:otherwise>--%>
<%--							<b>passou 4</b>--%>
<%--							<%--%>
<%--							infoItemRenderer.render(infoListObject, request, PipingServletResponseFactory.createPipingServletResponse(pageContext));--%>
<%--							%>--%>

<%--						</c:otherwise>--%>
<%--					</c:choose>--%>
<%--				</li>--%>

<%--			<%--%>
<%--			}--%>
<%--			%>--%>

<%--		</ul>--%>
	</c:otherwise>
</c:choose>