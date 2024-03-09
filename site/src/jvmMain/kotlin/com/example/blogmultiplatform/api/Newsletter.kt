package com.example.blogmultiplatform.api

import com.example.blogmultiplatform.data.MongoDB
import com.example.blogmultiplatform.models.Newsletter
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue

@Api(routeOverride = "subscribe")
suspend fun subscribeNewsletter(context: ApiContext) {
    context.runCatching {
        req.getBody<Newsletter>()
            ?.also { newsletter ->
                res.setBody(data.getValue<MongoDB>().subscribe(newsletter = newsletter))
            }
    }.onFailure { e ->
        context.logger.info("readSponsoredPosts API EXCEPTION: $e")
        context.res.setBody(e.message ?: "subscribeNewsletter API error")
    }
}