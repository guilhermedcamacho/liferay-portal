/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sample;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import net.datafaker.Faker;
import net.datafaker.providers.base.Name;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;
import org.json.JSONArray;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RequestMapping("/categories")
@RestController
public class CategoryRestController extends BaseRestController {

	@DeleteMapping(
		"/{objectDefinitionExternalReferenceCode}/{externalReferenceCode}"
	)
	public ResponseEntity<String> delete(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String objectDefinitionExternalReferenceCode,
		@PathVariable String externalReferenceCode,
		@RequestParam Map<String, String> parameters) {

		log(jwt, _log, parameters);

		String category = WebClient.create(
				"https://api.escuelajs.co/api/v1/categories/"+externalReferenceCode
		).delete(
		).accept(
				MediaType.APPLICATION_JSON
		).retrieve(
		).bodyToMono(
				String.class
		).block();

		return new ResponseEntity<>(category, HttpStatus.OK);
	}

	private List<LiferayCategoryResponse> parseCategories(JSONArray jsonArray) {
		List<LiferayCategoryResponse> liferayCategories  = new ArrayList<LiferayCategoryResponse>();

		jsonArray.iterator().forEachRemaining(jsonObject -> {
			liferayCategories.add(parseCategory((JSONObject)jsonObject));
		});

		return liferayCategories;
	}

	private LiferayCategoryResponse parseCategory(JSONObject jsonObject) {
		LiferayCategoryResponse liferayCategory = new LiferayCategoryResponse();

		long id = jsonObject.getLong("id");
		liferayCategory.setIdentifier(id);
		liferayCategory.setExternalReferenceCode(String.valueOf(id));
		liferayCategory.setName(jsonObject.getString("name"));
		liferayCategory.setImageURL(jsonObject.getString("image"));

		return liferayCategory;
	}


	@GetMapping("/{objectDefinitionExternalReferenceCode}")
	public ResponseEntity<String> get(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String objectDefinitionExternalReferenceCode,
		@RequestParam Map<String, String> parameters) {

		log(jwt, _log, parameters);

		String categories = WebClient.create(
				"https://api.escuelajs.co/api/v1/categories?limit=100"
		).get(
		).accept(
				MediaType.APPLICATION_JSON
		).retrieve(
		).bodyToMono(
				String.class
		).block();

		JSONArray jsonArray = new JSONArray(categories);

		List<LiferayCategoryResponse> liferayCategories = parseCategories(jsonArray);

		return new ResponseEntity<>(
				new JSONObject(
				).put(
						"items", new JSONArray(liferayCategories)
				).put(
						"totalCount", liferayCategories.size()
				).toString(),
				HttpStatus.OK);
	}

	@GetMapping(
		"/{objectDefinitionExternalReferenceCode}/{externalReferenceCode}"
	)
	public ResponseEntity<String> get(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String objectDefinitionExternalReferenceCode,
		@PathVariable String externalReferenceCode,
		@RequestParam Map<String, String> parameters) {

		log(jwt, _log, parameters);

		String category = WebClient.create(
				"https://api.escuelajs.co/api/v1/categories/"+externalReferenceCode
		).get(
		).accept(
				MediaType.APPLICATION_JSON
		).retrieve(
		).bodyToMono(
				String.class
		).block();

		JSONObject jsonObject = new JSONObject(category);

		LiferayCategoryResponse liferayCategoryResponse = parseCategory(jsonObject);

		if (liferayCategoryResponse == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(
				new JSONObject(liferayCategoryResponse).toString(), HttpStatus.OK);
	}

	@PostMapping("/{objectDefinitionExternalReferenceCode}")
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String objectDefinitionExternalReferenceCode,
		@RequestBody String json) {

		log(jwt, _log, json);

		JSONObject jsonObject = new JSONObject(json);

		JSONObject objectEntryJSONObject = jsonObject.getJSONObject(
				"objectEntry");

		String category = WebClient.create(
				"https://api.escuelajs.co/api/v1/categories/"
		).post(
		).accept(
				MediaType.APPLICATION_JSON
		).contentType(
				MediaType.APPLICATION_JSON
		).bodyValue(
				new JSONObject(
				).put(
						"name",
						objectEntryJSONObject.getString("name")
				).put(
						"image",
						objectEntryJSONObject.getString("imageURL")
				).toString()
		).retrieve(
		).bodyToMono(
				String.class
		).block();

		JSONObject categoryJSONObject = new JSONObject(category);

		LiferayCategoryResponse liferayCategoryResponse = parseCategory(categoryJSONObject);

		return new ResponseEntity<>(
				new JSONObject(liferayCategoryResponse).toString(),
				HttpStatus.OK);
	}

	@PutMapping(
		"/{objectDefinitionExternalReferenceCode}/{externalReferenceCode}"
	)
	public ResponseEntity<String> put(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String objectDefinitionExternalReferenceCode,
		@PathVariable String externalReferenceCode, @RequestBody String json) {

		log(jwt, _log, json);

		JSONObject jsonObject = new JSONObject(json);

		JSONObject objectEntryJSONObject = jsonObject.getJSONObject(
				"objectEntry");

		String category = WebClient.create(
				"https://api.escuelajs.co/api/v1/categories/"+externalReferenceCode
		).put(
		).accept(
				MediaType.APPLICATION_JSON
		).contentType(
				MediaType.APPLICATION_JSON
		).bodyValue(
				new JSONObject(
				).put(
						"id",
						objectEntryJSONObject.getLong("identifier")
				).put(
						"name",
						objectEntryJSONObject.getString("name")
				).put(
						"image",
						objectEntryJSONObject.getString("imageURL")
				).toString()
		).retrieve(
		).bodyToMono(
				String.class
		).block();

		JSONObject categoryJSONObject = new JSONObject(category);

		LiferayCategoryResponse liferayCategoryResponse = parseCategory(categoryJSONObject);

		return new ResponseEntity<>(
				new JSONObject(liferayCategoryResponse).toString(),
				HttpStatus.OK);
	}

	private static final Log _log = LogFactory.getLog(
		CategoryRestController.class);

}