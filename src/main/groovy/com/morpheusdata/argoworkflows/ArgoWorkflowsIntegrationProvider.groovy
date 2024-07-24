package com.morpheusdata.argoworkflows

import com.morpheusdata.core.providers.AbstractGenericIntegrationProvider
import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.views.HTMLResponse
import com.morpheusdata.model.OptionType
import com.morpheusdata.views.ViewModel
import com.morpheusdata.model.AccountIntegration
import groovy.util.logging.Slf4j
import com.morpheusdata.model.*
import com.morpheusdata.core.util.RestApiUtil
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

@Slf4j
class ArgoWorkflowsIntegrationProvider extends AbstractGenericIntegrationProvider {
	Plugin plugin
	MorpheusContext morpheus
	RestApiUtil argoAPI

	String code = 'argoworkflows'
	String name = 'Argo Workflows'

	ArgoWorkflowsIntegrationProvider(Plugin plugin, MorpheusContext context) {
		this.plugin = plugin
		this.morpheus = context
		this.argoAPI = new RestApiUtil()
	}

    @Override
    MorpheusContext getMorpheus() {
		return this.morpheus
    }

    @Override
    Plugin getPlugin() {
        return plugin
    }

    @Override
    String getCode() {
        return "argoWorkflows"
    }

    @Override
    String getName() {
        return "Argo Workflows"
    }

	@Override
	String getCategory() {
		return 'monitoring'
	}

	@Override
	Icon getIcon() {
		// return new Icon()
		return new Icon(path:"argo-horizontal-color.png", darkPath: "argo-horizontal-color.png")
	}

	@Override
	void refresh(AccountIntegration accountIntegration) {
		log.info "Syncing Stuff"
		accountIntegration.setServiceConfig("Testinstuff")
		log.debug "daily refresh run for ${accountIntegration}"
	}

	@Override
	List<OptionType> getOptionTypes() {
		OptionType apiUrl = new OptionType(
			name: 'Argo Workflows API Endpoint',
			code: 'argo-workflows-api',
			fieldName: 'serviceUrl',
			placeHolder: 'https://10.0.0.100:6443',
			defaultValue: 'https://10.0.0.151:31366',
			displayOrder: 0,
			fieldLabel: 'Argo Workflows URL',
			required: true,
			inputType: OptionType.InputType.TEXT
		)
		OptionType authTypes = new OptionType(
			name: 'Argo Workflows Auth Type',
			code: 'argo-workflows-auth-type',
			fieldName: 'argoWorkflowsAuthType',
			displayOrder: 1,
			fieldLabel: 'Auth Type',
			optionSource: 'authenticationTypes',
			required: true,
			inputType: OptionType.InputType.SELECT
		)
		OptionType apiUsername = new OptionType(
			name: 'Argo Workflows Username',
			code: 'argo-workflows-username',
			fieldName: 'argoWorkflowsUsername',
			placeHolder: 'admin',
			displayOrder: 2,
			fieldLabel: 'Username',
			required: false,
			inputType: OptionType.InputType.TEXT
		)
		OptionType apiPassword = new OptionType(
			name: 'Argo Workflows Username',
			code: 'argo-workflows-password',
			fieldName: 'serviceToken',
			displayOrder: 3,
			fieldLabel: 'Password',
			required: false,
			inputType: OptionType.InputType.PASSWORD
		)
		return [apiUrl, authTypes, apiUsername, apiPassword]
	}

	@Override
	HTMLResponse renderTemplate(AccountIntegration integration) {
		log.info "Integration Details: ${integration.account}"
		log.info "Integration Details: ${integration.config}"
		log.info "Integration Details: ${integration.id}"
		//println "Integration Details: ${integration.Status}"
		log.info "Integration Details: ${integration.objectRefs}"
		log.info "Integration Details: ${integration.serviceConfig}"
		log.info("Integration Details, plugin icon: ${this.getIcon().getPath()}")

		// Define an object for storing the data retrieved from the Argo Workflows REST API
		def HashMap<String, String> argoPayload = new HashMap<String, String>();

		JsonSlurper slurper = new JsonSlurper()
		def integrationJson = slurper.parseText(integration.config)

		log.info "Integration JSON: ${integrationJson}"

		ViewModel<HashMap> model = new ViewModel<>()
		
		argoPayload.put("url", integrationJson["cm.plugin.argoWorkflowsAPI"])
		argoPayload.put("name", integration["name"])
		argoPayload.put("id", integrationJson["id"])
		argoPayload.put("iconPath", this.getIcon().getPath())
		log.info integration.getServiceConfig()
		integration.setServiceConfig("demotest")
        model.object = argoPayload
		getRenderer().renderTemplate("hbs/integration", model)
	}
}
