package com.morpheusdata.argoworkflows

import com.morpheusdata.core.AbstractOptionSourceProvider
import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.model.*
import groovy.util.logging.Slf4j
import com.morpheusdata.core.data.DataFilter
import com.morpheusdata.core.data.DataQuery

@Slf4j
class ArgoWorkflowsOptionSourceProvider extends AbstractOptionSourceProvider {

	Plugin plugin
	MorpheusContext morpheusContext

	ArgoWorkflowsOptionSourceProvider(Plugin plugin, MorpheusContext context) {
		this.plugin = plugin
		this.morpheusContext = context
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
		return new ArrayList<String>(['accountIntegrations'])
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
		//def options = morpheusContext.computeServer.listSyncProjections(tmpCloud.id).filter { it.category == "kubevirt.host.${tmpCloud.id}" }.map {[name: it.name, value: it.externalId]}.toSortedList {it.name}.blockingGet()
		return output
	}
}