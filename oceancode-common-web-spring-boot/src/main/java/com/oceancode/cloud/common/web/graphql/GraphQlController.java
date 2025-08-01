package com.oceancode.cloud.common.web.graphql;

import com.oceancode.cloud.common.constant.CommonConst;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConst.API_PREFIX)
public class GraphQlController {
    private GraphQL graphQL;

    @Autowired
    public void setGraphQL(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    @PostMapping("${oc.query.api:/graphql/query}")
    public Object query(@RequestBody QueryRequest param) {
        ExecutionResult result = this.graphQL.execute(param.getQuery());
        return GraphUtil.getData(result);
    }
}
