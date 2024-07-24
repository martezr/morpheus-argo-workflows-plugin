package com.morpheusdata.argoworkflows

import com.morpheusdata.core.AbstractOptionSourceProvider
import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.model.*
import groovy.util.logging.Slf4j
import com.morpheusdata.core.data.DataFilter
import com.morpheusdata.core.data.DataQuery
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import com.morpheusdata.core.util.HttpApiClient

@Slf4j
class ArgoWorkflowsOptionSourceProvider extends AbstractOptionSourceProvider {

	Plugin plugin
	MorpheusContext morpheusContext
	HttpApiClient argoWorkflowsAPI

	ArgoWorkflowsOptionSourceProvider(Plugin plugin, MorpheusContext context) {
		this.plugin = plugin
		this.morpheusContext = context
		this.argoWorkflowsAPI = new HttpApiClient()
	}

	@Override
	MorpheusContext getMorpheus() {
		return this.morpheusContext
	}

	@Override
	Plugin getPlugin() {
		return this.plugin
	}

	@Override
	String getCode() {
		return 'argo-workflows-option-source'
	}

	@Override
	String getName() {
		return 'Argo Workflows Option Source'
	}

	@Override
	List<String> getMethodNames() {
		return new ArrayList<String>(['accountIntegrations','argoWorkflowTemplates','authenticationTypes'])
	}

	def accountIntegrations(args){
        log.info "OP ARG: ${args}"
		def integrations = morpheusContext.async.accountIntegration.list(new DataQuery().withFilter('type', 'argoworkflows')).toList().blockingGet()
        log.info "Integrations: ${integrations}"
        def output = []
        for(integration in integrations){
            def test = [:]
            log.info "Integration: ${integration.name}"
            test["name"] = integration.name
            test["value"] = integration.name
            output << test
        }
		return output
	}

	def argoWorkflowTemplates(args){
        log.info "WORKFLOW TEMPLATE ARGs: ${args}"
		def integrationName = args["task"]["taskOptions.argoWorkflowIntegration"][0]
		log.info "INTEGRATION OL TASK ${integrationName}"
		def integrations = morpheusContext.async.accountIntegration.list(new DataQuery().withFilters(new DataFilter("type", "argoworkflows"), new DataFilter("name", integrationName))).toList().blockingGet()
		def serviceURL = ""
        for(integration in integrations){
			log.info "Integration: ${integration.config}"
			JsonSlurper slurper = new JsonSlurper()
			def settingsJson = slurper.parseText(integration.config)
			serviceURL = settingsJson.cm.plugin.serviceUrl
			log.info "Service URL: ${settingsJson.cm.plugin.serviceUrl}"
		}
		//def results = argoWorkflowsAPI.callApi("https://dummy.restapiexample.com", "api/v1/employees", "", "", new RestApiUtil.RestOptions(headers:['Content-Type':'application/json'], ignoreSSL: true) , 'GET')
		def apiResults = argoWorkflowsAPI.callJsonApi(serviceURL, "api/v1/workflow-templates/argo", new HttpApiClient.RequestOptions(contentType: "application/json", ignoreSSL: true), 'GET')
		//JsonSlurper jslurper = new JsonSlurper()
		//def json = jslurper.parseText(apiResults.content)

		//def results = argoWorkflowsAPI.callApi(serviceURL, "api/v1/workflow-templates/argo", "", "", new RestApiUtil.RestOptions(headers:['Content-Type':'application/json'], ignoreSSL: true), 'GET')
		def workflowTemplates = apiResults.data.items
		for (wftemplate in workflowTemplates){
			log.info "API: ${wftemplate}"
			log.info "API RESULTS: ${wftemplate.metadata.name}"
			output << wftemplate.metadata.name
		}
		//{"cm.plugin.serviceUrl":"https://test.grt.local","cm.plugin.argoWorkflowsAuthType":"server","cm.plugin.argoWorkflowsUsername":"afmin","cm.plugin.serviceToken":"pawefwef","cm":{"plugin":{"serviceUrl":"https://test.grt.local","argoWorkflowsAuthType":"server","argoWorkflowsUsername":"afmin","serviceToken":"pawefwef"}}}
		def output = ["demo1"]

		log.info "OUTPUT: ${output}"
		return output
	}

	List authenticationTypes(args) {
		[
				[name: 'server', value: 'server'],
				[name: 'client', value: 'client'],
				[name: 'sso', value: 'sso']
		]
	}
}