package com.anasdidi.budget.api.graphql;

import com.anasdidi.budget.common.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.vertx.core.Promise;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.VertxDataFetcher;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.reactivex.ext.web.handler.graphql.GraphiQLHandler;

public class GraphqlVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(GraphqlVerticle.class);
  private final Router mainRouter;
  private final GraphqlDataFetcher dataFetcher;

  public GraphqlVerticle(Router mainRouter, EventBus eventBus) {
    this.mainRouter = mainRouter;
    this.dataFetcher = new GraphqlDataFetcher(eventBus);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    router.post("/").handler(GraphQLHandler.create(createGraphQL()));
    mainRouter.mountSubRouter("/graphql", router);

    Router router1 = Router.router(vertx);
    router1.post("/graphql").handler(GraphQLHandler.create(createGraphQL()));
    router1.get("/*").handler(GraphiQLHandler.create(new GraphiQLHandlerOptions()//
        .setGraphQLUri(AppConstants.CONTEXT_PATH + "/graphiql/graphql")//
        .setEnabled(true)));
    mainRouter.mountSubRouter("/graphiql", router1);

    logger.info("[start] Deployment success");
    startPromise.complete();
  }

  private GraphQL createGraphQL() {
    String schema = vertx.fileSystem().readFileBlocking("schema.graphql").toString();
    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

    RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()//
        .type("Query", builder -> builder//
            .dataFetcher("expense", new VertxDataFetcher<>(dataFetcher::expense)))//
        .build();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema =
        schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    return GraphQL.newGraphQL(graphQLSchema).build();
  }
}
