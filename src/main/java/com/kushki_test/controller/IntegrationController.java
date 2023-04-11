package com.kushki_test.controller;

import java.io.IOException;
import java.io.Serializable;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.kushki_test.model.Response;
import com.kushki_test.model.suscription;

@Controller
public class IntegrationController implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Value("${app.private-key}")
	private String privateKey;

	@GetMapping("/home")
	public String home(Model model) {
		return "home";
	}

	@PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String confirm(@ModelAttribute("response") Response response, BindingResult result, ModelMap model)
			throws IOException, UnirestException {
		if (result.hasErrors()) {
			return "error";
		}
		model.addAttribute("response", response);
		model.addAttribute("subscripcion", new suscription());
		return "subscription";
	}

	@PostMapping(value = "/subscription", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String subscription(@ModelAttribute suscription subscripcion, Model model)
			throws IOException, UnirestException {

		HttpResponse<JsonNode> response = Unirest.post("https://api-uat.kushkipagos.com/subscriptions/v1/card")
				.header("private-merchant-id", privateKey).header("content-type", "application/json")
				.body("{\"token\":\"" + subscripcion.getToken()
						+ "\",\"planName\":\"Premium\",\"periodicity\":\"monthly\",\"contactDetails\":{\"documentType\":\"CC\",\"documentNumber\":\""
						+ subscripcion.getDocument() + "\",\"email\":\"hola@hola.com\",\"firstName\":\""
						+ subscripcion.getName() + "\",\"lastName\":\"" + subscripcion.getLastName()
						+ "\",\"phoneNumber\":\"+593958736598\"},\"amount\":{\"subtotalIva\":70,\"subtotalIva0\":0,\"ice\":0,\"iva\":0.14,\"currency\":\"USD\"},\"startDate\":\"2023-04-11\",\"metadata\":{\"plan\":{\"tvpagada\":{\"Claro\":\"include\",\"telefonia\":\"include\",\"internet\":\"include\"}}}}")
				.asJson();

		Response datos = new Response();
		datos.setKushkiToken(subscripcion.getToken());
		model.addAttribute("response", datos);
		suscription salida = new suscription();
		salida.setMessage(response.getBody().toString());
		salida.setType("subscripcion");
		model.addAttribute("subscripcion", salida);
		return "subscription";
	}

	@PostMapping(value = "/preAuthorization", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String preAuthorization(@ModelAttribute suscription subscripcion, Model model)
			throws IOException, UnirestException {

		HttpResponse<JsonNode> response = Unirest.post("https://api-uat.kushkipagos.com/card/v1/preAuthorization")
				.header("private-merchant-id", privateKey).header("content-type", "application/json")
				.body("{\"token\":\"" + subscripcion.getToken()
						+ "\",\"amount\":{\"subtotalIva\":0,\"subtotalIva0\":100,\"ice\":0,\"iva\":0,\"currency\":\"PEN\"},\"fullResponse\":true}")
				.asJson();

		Response datos = new Response();
		datos.setKushkiToken(subscripcion.getToken());
		model.addAttribute("response", datos);
		suscription salida = new suscription();
		salida.setMessage(response.getBody().toString());
		salida.setType("pre-auth");
		model.addAttribute("subscripcion", salida);
		return "subscription";
	}

	@PostMapping(value = "/capPreAuthorization", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String capPreAuthorization(@ModelAttribute suscription subscripcion, Model model)
			throws IOException, UnirestException {

		HttpResponse<JsonNode> response = Unirest.post("https://api-uat.kushkipagos.com/card/v1/capture")
				.header("private-merchant-id", privateKey)
				.header("content-type", "application/json")
				.body("{\"ticketNumber\":\"" + subscripcion.getTicket()
						+ "\",\"amount\":{\"currency\":\"PEN\",\"subtotalIva\":0,\"iva\":0,\"subtotalIva0\":100,\"ice\":0},\"fullResponse\":true}")
				.asJson();

		Response datos = new Response();
		datos.setKushkiToken(subscripcion.getToken());
		model.addAttribute("response", datos);
		suscription salida = new suscription();
		salida.setMessage(response.getBody().toString());
		salida.setType("cap-pre-auth");
		model.addAttribute("subscripcion", salida);
		return "subscription";
	}

	@PostMapping(value = "/getSubscription", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String getSubscription(@ModelAttribute suscription subscripcion, Model model)
			throws IOException, UnirestException {

		HttpResponse<JsonNode> response = Unirest
				.get("https://api-uat.kushkipagos.com/subscriptions/v1/card/search/" + subscripcion.getIdSubscription())
				.header("private-merchant-id", privateKey).asJson();

		Response datos = new Response();
		datos.setKushkiToken(subscripcion.getToken());
		model.addAttribute("response", datos);
		suscription salida = new suscription();
		salida.setMessage(response.getBody().toString());
		salida.setType("get-subscription");
		model.addAttribute("subscripcion", salida);
		return "subscription";
	}

	@PostMapping(value = "/voidTransaction", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String voidTransaction(@ModelAttribute suscription subscripcion, Model model)
			throws IOException, UnirestException {

		HttpResponse<JsonNode> response = Unirest
				.delete("https://api-uat.kushkipagos.com/v1/charges/" + subscripcion.getTicket())
				.header("private-merchant-id", privateKey)	
				.body("{\"fullResponse\": true}")				
				.asJson();

		Response datos = new Response();
		datos.setKushkiToken(subscripcion.getToken());
		model.addAttribute("response", datos);
		suscription salida = new suscription();
		salida.setMessage(response.getBody().toString());
		salida.setType("void-subscription");
		model.addAttribute("subscripcion", salida);
		return "subscription";
	}

	@PostMapping(value = "/getTransactionList", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String getTransactionList(@ModelAttribute suscription subscripcion, Model model)
			throws IOException, UnirestException {

		HttpResponse<JsonNode> response = Unirest
				  .get("https://api-uat.kushkipagos.com/analytics/v1/transactions-list?" + subscripcion.getParam())
				  .header("private-merchant-id", privateKey)
				  .asJson();

		Response datos = new Response();
		datos.setKushkiToken(subscripcion.getToken());
		model.addAttribute("response", datos);
		suscription salida = new suscription();
		salida.setMessage(response.getBody().toString());
		salida.setType("get-trx-list");
		model.addAttribute("subscripcion", salida);
		return "subscription";
	}
}
