package org.egov.bpa.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.bpa.config.BPAConfiguration;
import org.egov.bpa.web.model.idgen.IdGenerationRequest;
import org.egov.bpa.web.model.idgen.IdGenerationResponse;
import org.egov.bpa.web.model.idgen.IdRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Repository
public class IdGenRepository {

	private RestTemplate restTemplate;

	private BPAConfiguration config;

	@Autowired
	public IdGenRepository(RestTemplate restTemplate, BPAConfiguration config) {
		this.restTemplate = restTemplate;
		this.config = config;
	}

	/**
	 * Call iDgen to generateIds
	 * 
	 * @param requestInfo
	 *            The rquestInfo of the request
	 * @param tenantId
	 *            The tenantiD of the bpa
	 * @param name
	 *            Name of the foramt
	 * @param format
	 *            Format of the ids
	 * @param count
	 *            Total Number of idGen ids required
	 * @return
	 */
	public IdGenerationResponse getId(RequestInfo requestInfo, String tenantId, String name, String format, int count) {

		List<IdRequest> reqList = new ArrayList<>();
		reqList.add(IdRequest.builder().idName(name).format(format).tenantId(tenantId).build());
		IdGenerationRequest req = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
		IdGenerationResponse response = null;
		try {
			response = restTemplate.postForObject(config.getIdGenHost() + config.getIdGenPath(), req,
					IdGenerationResponse.class);
		} catch (HttpClientErrorException e) {
			throw new ServiceCallException(e.getResponseBodyAsString());
		}
		return response;
	}

}
