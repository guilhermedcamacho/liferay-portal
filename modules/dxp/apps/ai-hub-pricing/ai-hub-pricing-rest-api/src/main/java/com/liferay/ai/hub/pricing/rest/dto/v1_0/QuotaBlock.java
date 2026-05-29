/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Carolina Barbosa
 * @generated
 */
@Generated("")
@GraphQLName("QuotaBlock")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"size", "transactionId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "QuotaBlock")
public class QuotaBlock implements Serializable {

	public static QuotaBlock toDTO(String json) {
		return ObjectMapperUtil.readValue(QuotaBlock.class, json);
	}

	public static QuotaBlock unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(QuotaBlock.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getAiHubQuotaConversionTableVersion() {
		if (_aiHubQuotaConversionTableVersionSupplier != null) {
			aiHubQuotaConversionTableVersion =
				_aiHubQuotaConversionTableVersionSupplier.get();

			_aiHubQuotaConversionTableVersionSupplier = null;
		}

		return aiHubQuotaConversionTableVersion;
	}

	public void setAiHubQuotaConversionTableVersion(
		Double aiHubQuotaConversionTableVersion) {

		this.aiHubQuotaConversionTableVersion =
			aiHubQuotaConversionTableVersion;

		_aiHubQuotaConversionTableVersionSupplier = null;
	}

	@JsonIgnore
	public void setAiHubQuotaConversionTableVersion(
		UnsafeSupplier<Double, Exception>
			aiHubQuotaConversionTableVersionUnsafeSupplier) {

		_aiHubQuotaConversionTableVersionSupplier = () -> {
			try {
				return aiHubQuotaConversionTableVersionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double aiHubQuotaConversionTableVersion;

	@JsonIgnore
	private Supplier<Double> _aiHubQuotaConversionTableVersionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getPurchaseDate() {
		if (_purchaseDateSupplier != null) {
			purchaseDate = _purchaseDateSupplier.get();

			_purchaseDateSupplier = null;
		}

		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;

		_purchaseDateSupplier = null;
	}

	@JsonIgnore
	public void setPurchaseDate(
		UnsafeSupplier<Date, Exception> purchaseDateUnsafeSupplier) {

		_purchaseDateSupplier = () -> {
			try {
				return purchaseDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date purchaseDate;

	@JsonIgnore
	private Supplier<Date> _purchaseDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getPurchaseExpirationDate() {
		if (_purchaseExpirationDateSupplier != null) {
			purchaseExpirationDate = _purchaseExpirationDateSupplier.get();

			_purchaseExpirationDateSupplier = null;
		}

		return purchaseExpirationDate;
	}

	public void setPurchaseExpirationDate(Date purchaseExpirationDate) {
		this.purchaseExpirationDate = purchaseExpirationDate;

		_purchaseExpirationDateSupplier = null;
	}

	@JsonIgnore
	public void setPurchaseExpirationDate(
		UnsafeSupplier<Date, Exception> purchaseExpirationDateUnsafeSupplier) {

		_purchaseExpirationDateSupplier = () -> {
			try {
				return purchaseExpirationDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date purchaseExpirationDate;

	@JsonIgnore
	private Supplier<Date> _purchaseExpirationDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getRemainingBalance() {
		if (_remainingBalanceSupplier != null) {
			remainingBalance = _remainingBalanceSupplier.get();

			_remainingBalanceSupplier = null;
		}

		return remainingBalance;
	}

	public void setRemainingBalance(Integer remainingBalance) {
		this.remainingBalance = remainingBalance;

		_remainingBalanceSupplier = null;
	}

	@JsonIgnore
	public void setRemainingBalance(
		UnsafeSupplier<Integer, Exception> remainingBalanceUnsafeSupplier) {

		_remainingBalanceSupplier = () -> {
			try {
				return remainingBalanceUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer remainingBalance;

	@JsonIgnore
	private Supplier<Integer> _remainingBalanceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getSize() {
		if (_sizeSupplier != null) {
			size = _sizeSupplier.get();

			_sizeSupplier = null;
		}

		return size;
	}

	public void setSize(Integer size) {
		this.size = size;

		_sizeSupplier = null;
	}

	@JsonIgnore
	public void setSize(UnsafeSupplier<Integer, Exception> sizeUnsafeSupplier) {
		_sizeSupplier = () -> {
			try {
				return sizeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Integer size;

	@JsonIgnore
	private Supplier<Integer> _sizeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTransactionId() {
		if (_transactionIdSupplier != null) {
			transactionId = _transactionIdSupplier.get();

			_transactionIdSupplier = null;
		}

		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;

		_transactionIdSupplier = null;
	}

	@JsonIgnore
	public void setTransactionId(
		UnsafeSupplier<String, Exception> transactionIdUnsafeSupplier) {

		_transactionIdSupplier = () -> {
			try {
				return transactionIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String transactionId;

	@JsonIgnore
	private Supplier<String> _transactionIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof QuotaBlock)) {
			return false;
		}

		QuotaBlock quotaBlock = (QuotaBlock)object;

		return Objects.equals(toString(), quotaBlock.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Double aiHubQuotaConversionTableVersion =
			getAiHubQuotaConversionTableVersion();

		if (aiHubQuotaConversionTableVersion != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aiHubQuotaConversionTableVersion\": ");

			sb.append(aiHubQuotaConversionTableVersion);
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Date purchaseDate = getPurchaseDate();

		if (purchaseDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"purchaseDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(purchaseDate));

			sb.append("\"");
		}

		Date purchaseExpirationDate = getPurchaseExpirationDate();

		if (purchaseExpirationDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"purchaseExpirationDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(purchaseExpirationDate));

			sb.append("\"");
		}

		Integer remainingBalance = getRemainingBalance();

		if (remainingBalance != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"remainingBalance\": ");

			sb.append(remainingBalance);
		}

		Integer size = getSize();

		if (size != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append(size);
		}

		String transactionId = getTransactionId();

		if (transactionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transactionId\": ");

			sb.append("\"");

			sb.append(_escape(transactionId));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.ai.hub.pricing.rest.dto.v1_0.QuotaBlock",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
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
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-915395651