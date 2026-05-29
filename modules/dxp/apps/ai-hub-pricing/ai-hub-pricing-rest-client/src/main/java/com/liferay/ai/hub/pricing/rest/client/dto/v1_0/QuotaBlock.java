/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.rest.client.dto.v1_0;

import com.liferay.ai.hub.pricing.rest.client.function.UnsafeSupplier;
import com.liferay.ai.hub.pricing.rest.client.serdes.v1_0.QuotaBlockSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Carolina Barbosa
 * @generated
 */
@Generated("")
public class QuotaBlock implements Cloneable, Serializable {

	public static QuotaBlock toDTO(String json) {
		return QuotaBlockSerDes.toDTO(json);
	}

	public Double getAiHubQuotaConversionTableVersion() {
		return aiHubQuotaConversionTableVersion;
	}

	public void setAiHubQuotaConversionTableVersion(
		Double aiHubQuotaConversionTableVersion) {

		this.aiHubQuotaConversionTableVersion =
			aiHubQuotaConversionTableVersion;
	}

	public void setAiHubQuotaConversionTableVersion(
		UnsafeSupplier<Double, Exception>
			aiHubQuotaConversionTableVersionUnsafeSupplier) {

		try {
			aiHubQuotaConversionTableVersion =
				aiHubQuotaConversionTableVersionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double aiHubQuotaConversionTableVersion;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public void setPurchaseDate(
		UnsafeSupplier<Date, Exception> purchaseDateUnsafeSupplier) {

		try {
			purchaseDate = purchaseDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date purchaseDate;

	public Date getPurchaseExpirationDate() {
		return purchaseExpirationDate;
	}

	public void setPurchaseExpirationDate(Date purchaseExpirationDate) {
		this.purchaseExpirationDate = purchaseExpirationDate;
	}

	public void setPurchaseExpirationDate(
		UnsafeSupplier<Date, Exception> purchaseExpirationDateUnsafeSupplier) {

		try {
			purchaseExpirationDate = purchaseExpirationDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date purchaseExpirationDate;

	public Integer getRemainingBalance() {
		return remainingBalance;
	}

	public void setRemainingBalance(Integer remainingBalance) {
		this.remainingBalance = remainingBalance;
	}

	public void setRemainingBalance(
		UnsafeSupplier<Integer, Exception> remainingBalanceUnsafeSupplier) {

		try {
			remainingBalance = remainingBalanceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer remainingBalance;

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setSize(UnsafeSupplier<Integer, Exception> sizeUnsafeSupplier) {
		try {
			size = sizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer size;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public void setTransactionId(
		UnsafeSupplier<String, Exception> transactionIdUnsafeSupplier) {

		try {
			transactionId = transactionIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String transactionId;

	@Override
	public QuotaBlock clone() throws CloneNotSupportedException {
		return (QuotaBlock)super.clone();
	}

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
		return QuotaBlockSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-83454441