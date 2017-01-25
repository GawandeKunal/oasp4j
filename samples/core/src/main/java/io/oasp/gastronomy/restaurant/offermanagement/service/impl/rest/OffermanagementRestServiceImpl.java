package io.oasp.gastronomy.restaurant.offermanagement.service.impl.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import io.oasp.gastronomy.restaurant.general.logic.api.to.BinaryObjectEto;
import io.oasp.gastronomy.restaurant.general.logic.base.UcManageBinaryObject;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.Offermanagement;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.DrinkEto;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.MealEto;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.OfferEto;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.OfferFilter;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.OfferSearchCriteriaTo;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.OfferSortBy;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.ProductEto;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.ProductFilter;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.ProductSearchCriteriaTo;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.ProductSortBy;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.SideDishEto;
import io.oasp.gastronomy.restaurant.offermanagement.service.api.rest.OffermanagementRestService;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

/**
 */
@Transactional
@Named("OffermanagementRestService")
public class OffermanagementRestServiceImpl implements OffermanagementRestService {

  private Offermanagement offermanagement;

  @Inject
  private UcManageBinaryObject ucManageBinaryObject;

  /**
   * @return ucManageBinaryObject
   */
  public UcManageBinaryObject getUcManageBinaryObject() {

    return this.ucManageBinaryObject;
  }

  /**
   * @param offerManagement the offerManagement to be set
   */
  @Inject
  public void setOffermanagement(Offermanagement offerManagement) {

    this.offermanagement = offerManagement;
  }

  @Override
  public OfferEto getOffer(long id) {

    return this.offermanagement.findOffer(id);
  }

  @Override
  public OfferEto saveOffer(OfferEto offer) {

    return this.offermanagement.saveOffer(offer);
  }

  @Override
  @Deprecated
  public OfferEto updateOffer(OfferEto offer) {

    return this.offermanagement.saveOffer(offer);
  }

  @Override
  @Deprecated
  public List<OfferEto> getAllOffers() {

    return this.offermanagement.findAllOffers();
  }

  @Override
  @Deprecated
  public List<ProductEto> getAllProducts() {

    return this.offermanagement.findAllProducts();
  }

  @Override
  public ProductEto saveProduct(ProductEto product) {

    return this.offermanagement.saveProduct(product);
  }

  @Override
  @Deprecated
  public List<MealEto> getAllMeals() {

    return this.offermanagement.findAllMeals();
  }

  @Override
  @Deprecated
  public List<DrinkEto> getAllDrinks() {

    return this.offermanagement.findAllDrinks();
  }

  @Override
  @Deprecated
  public List<SideDishEto> getAllSideDishes() {

    return this.offermanagement.findAllSideDishes();
  }

  @Override
  public void deleteOffer(long id) {

    this.offermanagement.deleteOffer(id);
  }

  @Override
  public ProductEto findProductByRevision(long id, Long revision) {

    if (revision != null) {
      return this.offermanagement.findProductByRevision(id, revision);
    } else {
      return this.offermanagement.findProduct(id);
    }
  }

  @Override
  public ProductEto findProduct(long id) {

    return this.offermanagement.findProduct(id);
  }

  @Override
  @Deprecated
  public void updateProduct(ProductEto product) {

    this.offermanagement.saveProduct(product);
  }

  @Override
  public boolean isProductInUseByOffer(long id) {

    return this.offermanagement.isProductInUseByOffer(findProduct(id));
  }

  @Override
  public void deleteProduct(long id) {

    this.offermanagement.deleteProduct(id);
  }

  @Override
  @Deprecated
  public List<OfferEto> getFilteredOffers(OfferFilter offerFilter, OfferSortBy sortBy) {

    return this.offermanagement.findOffersFiltered(offerFilter, sortBy);
  }

  @Override
  @Deprecated
  public List<ProductEto> getFilteredProducts(ProductFilter productFilter, ProductSortBy sortBy) {

    return this.offermanagement.findProductsFiltered(productFilter, sortBy);
  }

  @Override
  public void updateProductPicture(long productId, BinaryObjectEto binaryObjectEto, InputStream picture)
      throws SerialException, SQLException, IOException {

    Blob blob = new SerialBlob(IOUtils.readBytesFromStream(picture));
    this.offermanagement.updateProductPicture(productId, blob, binaryObjectEto);

  }

  @Override
  public MultipartBody getProductPicture(long productId) throws SQLException, IOException {

    Blob blob = this.offermanagement.findProductPictureBlob(productId);
    // REVIEW arturk88 (hohwille) we need to find another way to stream the blob without loading into heap.
    // https://github.com/oasp/oasp4j-sample/pull/45
    byte[] data = IOUtils.readBytesFromStream(blob.getBinaryStream());

    List<Attachment> atts = new LinkedList<>();
    atts.add(new Attachment("binaryObjectEto", MediaType.APPLICATION_JSON,
        this.offermanagement.findProductPicture(productId)));
    atts.add(new Attachment("blob", MediaType.APPLICATION_OCTET_STREAM, new ByteArrayInputStream(data)));
    return new MultipartBody(atts, true);

  }

  @Override
  @Deprecated
  public Response getProductRawPicture(long productId) throws SQLException, IOException {

    Blob blob = this.offermanagement.findProductPictureBlob(productId);
    if (blob != null) {
      byte[] data = IOUtils.readBytesFromStream(blob.getBinaryStream());
      BinaryObjectEto metadata = this.offermanagement.findProductPicture(productId);
      return Response.ok(new ByteArrayInputStream(data)).header("Content-Type", metadata.getMimeType()).build();
    } else {
      return Response.noContent().build();
    }
  }

  @Override
  public void deleteProductPicture(long productId) {

    this.offermanagement.deleteProductPicture(productId);
  }

  @Override
  public PaginatedListTo<OfferEto> findOfferEtosByPost(OfferSearchCriteriaTo searchCriteriaTo) {

    return this.offermanagement.findOfferEtos(searchCriteriaTo);
  }

  @Override
  public PaginatedListTo<ProductEto> findProductEtosByPost(ProductSearchCriteriaTo searchCriteriaTo) {

    return this.offermanagement.findProductEtos(searchCriteriaTo);
  }

  @Override
  public Response getDownloadFile() throws SQLException, IOException {

    File filePdf = new File("D:/Users/kugawand/Desktop/DBIntegration.pdf");
    ResponseBuilder response = Response.ok(filePdf);
    response.header("Content-Disposition", "attachment; filename=test.pdf");
    return response.build();

  }

  @Override
  public Response uploadFile(List<Attachment> attachments) throws SQLException, IOException {

    BinaryObjectEto binaryObject = new BinaryObjectEto();
    Blob blob = null;
    for (Attachment attachment : attachments) {
      DataHandler handler = attachment.getDataHandler();
      try {
        InputStream stream = handler.getInputStream();
        OutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(stream, outputStream);
        byte[] byteArray = outputStream.toString().getBytes();
        if (byteArray != null && byteArray.length != 0) {
          blob = new SerialBlob(byteArray);
          getUcManageBinaryObject().saveBinaryObject(blob, binaryObject);
        }
      } catch (SQLException e) {
        throw new SQLException(e.getMessage(), e);
      } catch (IOException e) {
        throw new IOException(e.getMessage(), e);
      }
    }

    return Response.ok("file uploaded").build();
  }

}
