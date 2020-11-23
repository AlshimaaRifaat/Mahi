package com.mahitab.ecommerce.managers;

import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

public class ClientQuery {

    static Storefront.QueryRootQuery queryForShop() {
        return Storefront.query(
                rootQuery -> rootQuery
                        .shop(
                                shopQuery -> shopQuery
                                        .name()
                                        .description()
                                        .paymentSettings(
                                                payment -> payment
                                                        .countryCode()
                                                        .currencyCode()
                                                        .acceptedCardBrands()
                                        )
                                        .privacyPolicy(
                                                privacyQuery -> privacyQuery
                                                        .url()
                                        )
                                        .termsOfService(
                                                termsQuery -> termsQuery
                                                        .url()
                                        )

                        )
        );
    }

    static Storefront.QueryRootQuery queryHomeCollections() {
        return Storefront.query(
                rootQuery -> rootQuery
                        .shop(
                                shopQuery -> shopQuery
                                        .collections(
                                                arg -> arg.first(100),
                                                collectionsQuery -> collectionsQuery
                                                        .pageInfo(
                                                                colPageQuery -> colPageQuery
                                                                        .hasNextPage()
                                                        )
                                                        .edges(
                                                                colEdgeQuery -> colEdgeQuery
                                                                        .node(
                                                                                colNodeQuery -> colNodeQuery
                                                                                        .handle()
                                                                                        .title()
                                                                                        .updatedAt()
                                                                                        .products(
                                                                                                args -> args.first(4),
                                                                                                prodQuery -> prodQuery
                                                                                                        .edges(
                                                                                                                prodEdge -> prodEdge
                                                                                                                        .node(
                                                                                                                                prodEdgeQuery -> prodEdgeQuery
                                                                                                                                        .images(
                                                                                                                                                args -> args.first(4),
                                                                                                                                                productImgQuery -> productImgQuery
                                                                                                                                                        .edges(
                                                                                                                                                                pieq -> pieq
                                                                                                                                                                        .node(
                                                                                                                                                                                pienq -> pienq
                                                                                                                                                                                        .src()
                                                                                                                                                                        )
                                                                                                                                                        )
                                                                                                                                        )
                                                                                                                                        .title()
                                                                                                                                        .descriptionHtml()
                                                                                                                                        .publishedAt()
                                                                                                                                        .updatedAt()
                                                                                                                                        .tags()
                                                                                                                                        .variants(
                                                                                                                                                args -> args.first(4),
                                                                                                                                                productVariantQuery -> productVariantQuery
                                                                                                                                                        .edges(
                                                                                                                                                                variantEdge -> variantEdge
                                                                                                                                                                        .node(
                                                                                                                                                                                variantNode -> variantNode
                                                                                                                                                                                        .title()
                                                                                                                                                                                        .price()
                                                                                                                                                                                        .compareAtPrice()
                                                                                                                                                                                        .availableForSale()
                                                                                                                                                                                        .weight()
                                                                                                                                                                                        .weightUnit()
                                                                                                                                                                                        .sku()
                                                                                                                                                                                        .selectedOptions(
                                                                                                                                                                                                optionQuery -> optionQuery
                                                                                                                                                                                                        .name()
                                                                                                                                                                                                        .value()
                                                                                                                                                                                        )
                                                                                                                                                                        )
                                                                                                                                                        )
                                                                                                                                        )
                                                                                                                        )
                                                                                                        )
                                                                                        )
                                                                        )
                                                        )
                                        )
                        )
        );
    }

    static Storefront.QueryRootQuery queryCollectionsAllProducts() {
        return Storefront.query(
                rootQuery -> rootQuery
                        .shop(
                                shopQuery -> shopQuery
                                        .collections(
                                                arg -> arg.first(100),
                                                collectionsQuery -> collectionsQuery
                                                        .pageInfo(
                                                                colPageQuery -> colPageQuery
                                                                        .hasNextPage()
                                                        )
                                                        .edges(
                                                                colEdgeQuery -> colEdgeQuery
                                                                        .node(
                                                                                colNodeQuery -> colNodeQuery
                                                                                        .handle()
                                                                                        .title()
                                                                                        .updatedAt()
                                                                                        .products(
                                                                                                args -> args.first(250),
                                                                                                prodQuery -> prodQuery
                                                                                                        .edges(
                                                                                                                prodEdge -> prodEdge
                                                                                                                        .node(
                                                                                                                                prodEdgeQuery -> prodEdgeQuery
                                                                                                                                        .images(
                                                                                                                                                args -> args.first(250),
                                                                                                                                                productImgQuery -> productImgQuery
                                                                                                                                                        .edges(
                                                                                                                                                                pieq -> pieq
                                                                                                                                                                        .node(
                                                                                                                                                                                pienq -> pienq
                                                                                                                                                                                        .src()
                                                                                                                                                                        )
                                                                                                                                                        )
                                                                                                                                        )
                                                                                                                                        .title()
                                                                                                                                        .descriptionHtml()
                                                                                                                                        .publishedAt()
                                                                                                                                        .updatedAt()
                                                                                                                                        .tags()
                                                                                                                                        .variants(
                                                                                                                                                args -> args.first(250),
                                                                                                                                                productVariantQuery -> productVariantQuery
                                                                                                                                                        .edges(
                                                                                                                                                                variantEdge -> variantEdge
                                                                                                                                                                        .node(
                                                                                                                                                                                variantNode -> variantNode
                                                                                                                                                                                        .title()
                                                                                                                                                                                        .price()
                                                                                                                                                                                        .compareAtPrice()
                                                                                                                                                                                        .availableForSale()
                                                                                                                                                                                        .weight()
                                                                                                                                                                                        .weightUnit()
                                                                                                                                                                                        .sku()
                                                                                                                                                                                        .selectedOptions(
                                                                                                                                                                                                optionQuery -> optionQuery
                                                                                                                                                                                                        .name()
                                                                                                                                                                                                        .value()
                                                                                                                                                                                        )
                                                                                                                                                                        )
                                                                                                                                                        )
                                                                                                                                        )
                                                                                                                        )
                                                                                                        )
                                                                                        )
                                                                        )
                                                        )
                                        )
                        )
        );
    }

    static Storefront.QueryRootQuery queryAllCollections() {
        return Storefront.query(
                rootQuery -> rootQuery
                        .shop(
                                shopQuery -> shopQuery
                                        .collections(
                                                arg -> arg.first(600),
                                                collectionsQuery -> collectionsQuery
                                                        .pageInfo(
                                                                colPageQuery -> colPageQuery
                                                                        .hasNextPage()
                                                        )
                                                        .edges(
                                                                colEdgeQuery -> colEdgeQuery
                                                                        .node(
                                                                                colNodeQuery -> colNodeQuery
                                                                                        .handle()
                                                                                        .title()
                                                                                        .updatedAt()
                                                                                        .products(
                                                                                                args -> args.first(600),
                                                                                                prodQuery -> prodQuery
                                                                                                        .edges(
                                                                                                                prodEdge -> prodEdge
                                                                                                                        .node(
                                                                                                                                prodEdgeQuery -> prodEdgeQuery
                                                                                                                                        .images(
                                                                                                                                                args -> args.first(100),
                                                                                                                                                productImgQuery -> productImgQuery
                                                                                                                                                        .edges(
                                                                                                                                                                pieq -> pieq
                                                                                                                                                                        .node(
                                                                                                                                                                                pienq -> pienq
                                                                                                                                                                                        .src()
                                                                                                                                                                        )
                                                                                                                                                        )
                                                                                                                                        )
                                                                                                                                        .title()
                                                                                                                                        .descriptionHtml()
                                                                                                                                        .publishedAt()
                                                                                                                                        .updatedAt()
                                                                                                                                        .tags()
                                                                                                                                        .variants(
                                                                                                                                                args -> args.first(250),
                                                                                                                                                productVariantQuery -> productVariantQuery
                                                                                                                                                        .edges(
                                                                                                                                                                variantEdge -> variantEdge
                                                                                                                                                                        .node(
                                                                                                                                                                                variantNode -> variantNode
                                                                                                                                                                                        .title()
                                                                                                                                                                                        .price()
                                                                                                                                                                                        .compareAtPrice()
                                                                                                                                                                                        .availableForSale()
                                                                                                                                                                                        .weight()
                                                                                                                                                                                        .weightUnit()
                                                                                                                                                                                        .sku()
                                                                                                                                                                                        .selectedOptions(
                                                                                                                                                                                                optionQuery -> optionQuery
                                                                                                                                                                                                        .name()
                                                                                                                                                                                                        .value()
                                                                                                                                                                                        )
                                                                                                                                                                        )
                                                                                                                                                        )
                                                                                                                                        )
                                                                                                                        )
                                                                                                        )
                                                                                        )
                                                                        )
                                                        )
                                        )
                        )
        );
    }

    static Storefront.QueryRootQuery queryProducts(ID collectionID) {
        return Storefront.query(
                rootQuery -> rootQuery
                        .node(collectionID,
                                nodeQuery -> nodeQuery
                                        .onCollection(
                                                collectionQuery -> collectionQuery
                                                        .products(
                                                                arg -> arg.first(250),
                                                                productsQuery -> productsQuery
                                                                        .pageInfo(
                                                                                productPageQuery -> productPageQuery
                                                                                        .hasNextPage()
                                                                        )
                                                                        .edges(
                                                                                productEdgeQuery -> productEdgeQuery
                                                                                        .cursor()
                                                                                        .node(
                                                                                                productNodeQuery -> productNodeQuery
                                                                                                        .title()
                                                                                                        .descriptionHtml()
                                                                                                        .publishedAt()
                                                                                                        .updatedAt()
                                                                                                        .tags()
                                                                                                        .images(
                                                                                                                args -> args
                                                                                                                        .first(250),
                                                                                                                image -> image
                                                                                                                        .edges(
                                                                                                                                edge -> edge
                                                                                                                                        .node(
                                                                                                                                                node -> node
                                                                                                                                                        .src()
                                                                                                                                        )
                                                                                                                        )
                                                                                                        )
                                                                                                        .variants(
                                                                                                                args -> args.first(250),
                                                                                                                variantsQuery -> variantsQuery
                                                                                                                        .pageInfo(
                                                                                                                                variantsPageQuery -> variantsPageQuery
                                                                                                                                        .hasNextPage()
                                                                                                                        )
                                                                                                                        .edges(
                                                                                                                                variantsEdgeQuery -> variantsEdgeQuery
                                                                                                                                        .cursor()
                                                                                                                                        .node(
                                                                                                                                                variantsEdgeNodeQuery -> variantsEdgeNodeQuery
                                                                                                                                                        .title()
                                                                                                                                                        .price()
                                                                                                                                                        .compareAtPrice()
                                                                                                                                                        .sku()
                                                                                                                                                        .weight()
                                                                                                                                                        .weightUnit()
                                                                                                                                                        .availableForSale()
                                                                                                                                                        .selectedOptions(
                                                                                                                                                                opt -> opt
                                                                                                                                                                        .name()
                                                                                                                                                                        .value()
                                                                                                                                                        )
                                                                                                                                        )
                                                                                                                        )
                                                                                                        )
                                                                                        )
                                                                        )
                                                        )

                                        )
                        )
        );
    }

    static Storefront.MutationQuery mutationForCreateUser(
            String email, String password, String firstName, String lastName
    ) {

        Storefront.CustomerCreateInput input = new Storefront
                .CustomerCreateInput(email, password)
                .setFirstName(firstName)
                .setLastName(lastName);

        return ClientMutation.mutationForCreateUser(
                input
        );
    }
}
