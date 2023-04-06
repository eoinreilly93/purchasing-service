package com.shop.generic.purchasingservice.models;

import com.shop.generic.common.dtos.PurchaseProductDTO;
import java.util.List;
import java.util.UUID;

public record EnrichedPurchaseRequest(UUID purchaseId,
                                      List<PurchaseProductDTO> purchaseProductDTOList) {

}
