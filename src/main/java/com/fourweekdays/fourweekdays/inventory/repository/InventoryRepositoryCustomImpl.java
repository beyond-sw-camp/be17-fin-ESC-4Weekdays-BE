package com.fourweekdays.fourweekdays.inventory.repository;

import com.fourweekdays.fourweekdays.inventory.model.dto.request.InventorySearchRequest;
import com.fourweekdays.fourweekdays.inventory.model.dto.response.ProductInventoryResponse;
import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.product.exception.ProductException;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.model.entity.QProduct;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fourweekdays.fourweekdays.inbound.model.entity.QInbound.inbound;
import static com.fourweekdays.fourweekdays.inventory.model.entity.QInventory.inventory;
import static com.fourweekdays.fourweekdays.location.model.entity.QLocation.location;
import static com.fourweekdays.fourweekdays.product.model.entity.QProduct.product;
import static com.fourweekdays.fourweekdays.vendor.model.entity.QVendor.vendor;

@RequiredArgsConstructor
public class InventoryRepositoryCustomImpl implements InventoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Inventory> searchInventory(Pageable pageable, InventorySearchRequest request) {
        List<Inventory> inventories = queryFactory
                .selectFrom(inventory)
                .leftJoin(inventory.product, product).fetchJoin()
                .leftJoin(inventory.location, location).fetchJoin()
                .leftJoin(inventory.inbound, inbound).fetchJoin()
                .leftJoin(product.vendor, vendor).fetchJoin()
                .where(
                        inboundCodeEq(request.inboundCode()),
                        managerNameLike(request.inboundManagerName()),
                        productCodeEq(request.productCode()),
                        productNameLike(request.productName()),
                        locationCodeEq(request.locationCode()),
                        inboundAtBetween(request.createdFrom(), request.createdTo())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(inventory.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(inventory.count())
                .from(inventory)
                .leftJoin(inventory.product, product)
                .leftJoin(inventory.location, location)
                .leftJoin(inventory.inbound, inbound)
                .where(
                        inboundCodeEq(request.inboundCode()),
                        managerNameLike(request.inboundManagerName()),
                        productCodeEq(request.productCode()),
                        productNameLike(request.productName()),
                        locationCodeEq(request.locationCode()),
                        inboundAtBetween(request.createdFrom(), request.createdTo())
                )
                .fetchOne();

        return new PageImpl<>(inventories, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<ProductInventoryResponse> searchInventoryByProduct(Pageable pageable, InventorySearchRequest request) {

        // Product 기준으로 페이징
        List<Product> products = queryFactory
                .selectFrom(product)
                .distinct()
                .leftJoin(inventory).on(inventory.product.eq(product))
                .leftJoin(inventory.location, location)
                .leftJoin(inventory.inbound, inbound)
                .where(
                        productCodeEq(request.productCode()),
                        productNameLike(request.productName()),
                        locationCodeEq(request.locationCode()),
                        inboundCodeEq(request.inboundCode()),
                        managerNameLike(request.inboundManagerName()),
                        inboundAtBetween(request.createdFrom(), request.createdTo()),
                        inventory.quantity.gt(0)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.productCode.asc())
                .fetch();

        // 각 Product의 Inventory 목록 조회
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();

        List<Inventory> inventories = queryFactory
                .selectFrom(inventory)
                .leftJoin(inventory.product, product).fetchJoin()
                .leftJoin(inventory.location, location).fetchJoin()
                .leftJoin(inventory.inbound, inbound).fetchJoin()
                .where(
                        product.id.in(productIds),
                        locationCodeEq(request.locationCode()),
                        inboundCodeEq(request.inboundCode()),
                        inboundAtBetween(request.createdFrom(), request.createdTo())
                )
                .orderBy(inventory.createdAt.desc())
                .fetch();

        // Product별 Inventory 그룹
        Map<Long, List<Inventory>> inventoryMap = inventories.stream()
                .collect(Collectors.groupingBy(
                        inv -> inv.getProduct().getId()
                ));

        // DTO 변환
        List<ProductInventoryResponse> results = products.stream()
                .map(p -> ProductInventoryResponse.from(
                        p,
                        inventoryMap.getOrDefault(p.getId(), List.of()))
                )
                .toList();

        // Total count
        Long total = queryFactory
                .select(product.countDistinct())
                .from(product)
                .leftJoin(inventory).on(inventory.product.eq(product))
                .leftJoin(inventory.location, location)
                .leftJoin(inventory.inbound, inbound)
                .where(
                        productCodeEq(request.productCode()),
                        productNameLike(request.productName()),
                        locationCodeEq(request.locationCode()),
                        inboundCodeEq(request.inboundCode()),
                        managerNameLike(request.inboundManagerName()),
                        inboundAtBetween(request.createdFrom(), request.createdTo()),
                        inventory.quantity.gt(0)
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
    }


    @Override
    public Optional<ProductInventoryResponse> findDetailByProductCode(String productCode) {
        Product findProduct = queryFactory
                .selectFrom(product)
                .leftJoin(product.vendor).fetchJoin()
                .where(product.productCode.eq(productCode))
                .fetchOne();


        if (findProduct == null) {
            return Optional.empty();
        }

        List<Inventory> inventories = queryFactory
                .selectFrom(inventory)
                .leftJoin(inventory.location).fetchJoin()
                .leftJoin(inventory.inbound).fetchJoin()
                .where(
                        productCodeEq(productCode)
                )
                .fetch();

        // 3. DTO 변환 (정적 팩토리 메서드 활용)
        return Optional.of(ProductInventoryResponse.from(findProduct, inventories));
    }


    //===== 조건 =====//
    private BooleanExpression productCodeEq(String productCode) {
        return StringUtils.hasText(productCode) ? product.productCode.eq(productCode) : null;
    }

    private BooleanExpression productNameLike(String productName) {
        return StringUtils.hasText(productName) ? product.name.contains(productName) : null;
    }

    private BooleanExpression locationCodeEq(String locationCode) {
        return StringUtils.hasText(locationCode) ? location.locationCode.eq(locationCode) : null;
    }

    private BooleanExpression inboundCodeEq(String inboundCode) {
        return StringUtils.hasText(inboundCode) ? inbound.inboundCode.eq(inboundCode) : null;
    }

    private BooleanExpression managerNameLike(String name) {
        return StringUtils.hasText(name) ? inbound.managerName.contains(name) : null;
    }

    private BooleanExpression inboundAtBetween(LocalDate from, LocalDate to) {
        if (from != null && to != null) {
            return inventory.createdAt.between(
                    from.atStartOfDay(),
                    to.atTime(23, 59, 59)
            );
        }

        if (from != null) {
            return inventory.createdAt.goe(from.atStartOfDay());
        }

        if (to != null) {
            return inventory.createdAt.loe(to.atTime(23, 59, 59));
        }

        return null;
    }
}
