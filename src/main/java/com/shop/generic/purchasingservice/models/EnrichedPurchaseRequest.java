package com.shop.generic.purchasingservice.models;

import com.shop.generic.common.valueobjects.PurchaseProductVO;
import java.util.List;
import java.util.UUID;

public record EnrichedPurchaseRequest(UUID purchaseId,
                                      List<PurchaseProductVO> purchaseProductVOList) {

}
