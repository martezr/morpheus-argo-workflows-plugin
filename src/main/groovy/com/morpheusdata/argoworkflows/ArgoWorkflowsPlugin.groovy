package com.morpheusdata.argoworkflows

import com.morpheusdata.core.Plugin
import com.morpheusdata.model.Permission
import com.morpheusdata.views.HandlebarsRenderer
import com.morpheusdata.views.ViewModel
import com.morpheusdata.web.Dispatcher
import com.morpheusdata.model.OptionType

class ArgoWorkflowsPlugin extends Plugin {

	@Override
	String getCode() {
		return 'argoworkflows-plugin'
	}

	@Override
	void initialize() {
		ArgoWorkflowsTaskProvider argoWorkflowsTaskProvider = new ArgoWorkflowsTaskProvider(this, morpheus)
		this.pluginProviders.put(argoWorkflowsTaskProvider.code, argoWorkflowsTaskProvider)
		ArgoWorkflowsIntegrationProvider argoWorkflowsIntegrationProvider = new ArgoWorkflowsIntegrationProvider(this, morpheus)
		this.pluginProviders.put(argoWorkflowsIntegrationProvider.code, argoWorkflowsIntegrationProvider)
		ArgoWorkflowsOptionSourceProvider argoWorkflowsOptionSourceProvider = new ArgoWorkflowsOptionSourceProvider(this, this.morpheus)
		this.pluginProviders.put(argoWorkflowsOptionSourceProvider.code, argoWorkflowsOptionSourceProvider)

		this.setName("Argo Workflows")
		this.setDescription("Argo Workflows automation integration")
		this.setAuthor("Martez Reed")
		this.setSourceCodeLocationUrl("https://github.com/martezr/morpheus-argo-workflows-plugin")
		this.setIssueTrackerUrl("https://github.com/martezr/morpheus-argo-workflows-plugin/issues")
	}

	@Override
	void onDestroy() {}
}