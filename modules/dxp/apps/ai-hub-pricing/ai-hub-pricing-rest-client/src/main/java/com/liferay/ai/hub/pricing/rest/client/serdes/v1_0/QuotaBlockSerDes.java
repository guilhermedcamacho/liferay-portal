/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.rest.client.serdes.v1_0;

import com.liferay.ai.hub.pricing.rest.client.dto.v1_0.QuotaBlock;
import com.liferay.ai.hub.pricing.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Carolina Barbosa
 * @generated
 */
@Generated("")
public class QuotaBlockSerDes {

	public static QuotaBlock toDTO(String json) {
		QuotaBlockJSONParser quotaBlockJSONParser = new QuotaBlockJSONParser();

		return quotaBlockJSONParser.parseToDTO(json);
	}

	public static QuotaBlock[] toDTOs(String json) {
		QuotaBlockJSONParser quotaBlockJSONParser = new QuotaBlockJSONParser();

		return quotaBlockJSONParser.parseToDTOs(json);
	}

	public static String toJSON(QuotaBlock quotaBlock) {
		if (quotaBlock == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (quotaBlock.getAiHubQuotaConversionTableVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aiHubQuotaConversionTableVersion\": ");

			sb.append(quotaBlock.getAiHubQuotaConversionTableVersion());
		}

		if (quotaBlock.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(quotaBlock.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (quotaBlock.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(quotaBlock.getId());
		}

		if (quotaBlock.getPurchaseDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"purchaseDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(quotaBlock.getPurchaseDate()));

			sb.append("\"");
		}

		if (quotaBlock.getPurchaseExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"purchaseExpirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					quotaBlock.getPurchaseExpirationDate()));

			sb.append("\"");
		}

		if (quotaBlock.getRemainingBalance() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"remainingBalance\": ");

			sb.append(quotaBlock.getRemainingBalance());
		}

		if (quotaBlock.getSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append(quotaBlock.getSize());
		}

		if (quotaBlock.getTransactionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transactionId\": ");

			sb.append("\"");

			sb.append(_escape(quotaBlock.getTransactionId()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		QuotaBlockJSONParser quotaBlockJSONParser = new QuotaBlockJSONParser();

		return quotaBlockJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(QuotaBlock quotaBlock) {
		if (quotaBlock == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (quotaBlock.getAiHubQuotaConversionTableVersion() == null) {
			map.put("aiHubQuotaConversionTableVersion", null);
		}
		else {
			map.put(
				"aiHubQuotaConversionTableVersion",
				String.valueOf(
					quotaBlock.getAiHubQuotaConversionTableVersion()));
		}

		if (quotaBlock.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(quotaBlock.getExternalReferenceCode()));
		}

		if (quotaBlock.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(quotaBlock.getId()));
		}

		if (quotaBlock.getPurchaseDate() == null) {
			map.put("purchaseDate", null);
		}
		else {
			map.put(
				"purchaseDate",
				liferayToJSONDateFormat.format(quotaBlock.getPurchaseDate()));
		}

		if (quotaBlock.getPurchaseExpirationDate() == null) {
			map.put("purchaseExpirationDate", null);
		}
		else {
			map.put(
				"purchaseExpirationDate",
				liferayToJSONDateFormat.format(
					quotaBlock.getPurchaseExpirationDate()));
		}

		if (quotaBlock.getRemainingBalance() == null) {
			map.put("remainingBalance", null);
		}
		else {
			map.put(
				"remainingBalance",
				String.valueOf(quotaBlock.getRemainingBalance()));
		}

		if (quotaBlock.getSize() == null) {
			map.put("size", null);
		}
		else {
			map.put("size", String.valueOf(quotaBlock.getSize()));
		}

		if (quotaBlock.getTransactionId() == null) {
			map.put("transactionId", null);
		}
		else {
			map.put(
				"transactionId", String.valueOf(quotaBlock.getTransactionId()));
		}

		return map;
	}

	public static class QuotaBlockJSONParser
		extends BaseJSONParser<QuotaBlock> {

		@Override
		protected QuotaBlock createDTO() {
			return new QuotaBlock();
		}

		@Override
		protected QuotaBlock[] createDTOArray(int size) {
			return new QuotaBlock[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "aiHubQuotaConversionTableVersion")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "purchaseDate")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "purchaseExpirationDate")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "remainingBalance")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "transactionId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			QuotaBlock quotaBlock, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "aiHubQuotaConversionTableVersion")) {

				if (jsonParserFieldValue != null) {
					quotaBlock.setAiHubQuotaConversionTableVersion(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					quotaBlock.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					quotaBlock.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "purchaseDate")) {
				if (jsonParserFieldValue != null) {
					quotaBlock.setPurchaseDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "purchaseExpirationDate")) {

				if (jsonParserFieldValue != null) {
					quotaBlock.setPurchaseExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "remainingBalance")) {
				if (jsonParserFieldValue != null) {
					quotaBlock.setRemainingBalance(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				if (jsonParserFieldValue != null) {
					quotaBlock.setSize(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "transactionId")) {
				if (jsonParserFieldValue != null) {
					quotaBlock.setTransactionId((String)jsonParserFieldValue);
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}
// LIFERAY-REST-BUILDER-HASH:1671584271