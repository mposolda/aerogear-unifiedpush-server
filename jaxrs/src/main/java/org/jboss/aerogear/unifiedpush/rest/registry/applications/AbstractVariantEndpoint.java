/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.unifiedpush.rest.registry.applications;

import org.jboss.aerogear.unifiedpush.api.Variant;
import org.jboss.aerogear.unifiedpush.rest.AbstractBaseEndpoint;
import org.jboss.aerogear.unifiedpush.service.GenericVariantService;
import org.jboss.aerogear.unifiedpush.service.PushApplicationService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.logging.Level;

import static org.jboss.aerogear.unifiedpush.rest.util.HttpRequestUtil.extractUsername;

/**
 * Abstract base class for all the concrete variant endpoints. Shares common
 * functionality.
 */
public abstract class AbstractVariantEndpoint extends AbstractBaseEndpoint {

    @Inject
    protected PushApplicationService pushAppService;

    @Inject
    protected GenericVariantService variantService;

    // Secret Reset
    @PUT
    @Path("/{variantId}/reset")
    @Consumes(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response resetSecret(@Context HttpServletRequest request, @PathParam("variantId") String variantId) {

        Variant variant = variantService.findByVariantIDForDeveloper(variantId, extractUsername(request));

        if (variant != null) {
            logger.log(Level.FINEST, "Resetting secret for: " + variant.getClass().getSimpleName());

            // generate the new 'secret' and apply it:
            String newSecret = UUID.randomUUID().toString();
            variant.setSecret(newSecret);
            variantService.updateVariant(variant);

            return Response.ok(variant).build();
        }

        return Response.status(Response.Status.NOT_FOUND).entity("Could not find requested Variant").build();
    }

    @GET
    @Path("/{variantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findVariantById(@Context HttpServletRequest request, @PathParam("variantId") String variantId) {

        Variant variant = variantService.findByVariantIDForDeveloper(variantId, extractUsername(request));

        if (variant != null) {
            return Response.ok(variant).build();
        }

        return Response.status(Response.Status.NOT_FOUND).entity("Could not find requested Variant").build();
    }

    // DELETE
    @DELETE
    @Path("/{variantId}")
    public Response deleteVariant(@Context HttpServletRequest request, @PathParam("variantId") String variantId) {

        Variant variant = variantService.findByVariantIDForDeveloper(variantId, extractUsername(request));

        if (variant != null) {
            logger.log(Level.FINEST, "Deleting: " + variant.getClass().getSimpleName());

            variantService.removeVariant(variant);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).entity("Could not find requested Variant").build();
    }
}