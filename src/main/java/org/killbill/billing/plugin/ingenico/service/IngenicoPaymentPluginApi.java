package org.killbill.billing.plugin.ingenico.service;

import com.ingenico.connect.gateway.sdk.java.Client;
import com.ingenico.connect.gateway.sdk.java.CommunicatorConfiguration;
import com.ingenico.connect.gateway.sdk.java.Factory;
import com.ingenico.connect.gateway.sdk.java.domain.payment.ApprovePaymentRequest;
import com.ingenico.connect.gateway.sdk.java.domain.payment.PaymentApprovalResponse;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.ApprovePaymentNonSepaDirectDebitPaymentMethodSpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.OrderApprovePayment;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.OrderReferencesApprovePayment;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillClock;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.payment.api.PaymentMethodPlugin;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.payment.plugin.api.*;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.billing.util.callcontext.TenantContext;
import org.killbill.billing.util.entity.Pagination;
import org.killbill.clock.Clock;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * Created by uengine on 2017. 8. 31..
 */
public class IngenicoPaymentPluginApi implements PaymentPluginApi {

    private OSGIKillbillAPI killbillApi;
    private OSGIKillbillClock clock;

    public IngenicoPaymentPluginApi(
            final OSGIKillbillAPI killbillApi,
            final OSGIKillbillClock clock
    ) {
        this.killbillApi = killbillApi;
        this.clock = clock;
    }

    @Override
    public PaymentTransactionInfoPlugin authorizePayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {

        //0단계. 위의 파라미터 들로 (kbAccountId 등등) 으로 인제니코의 클라이언트 키라든지, 토큰이라든지 값을 얻어와야한다.

        //1단계. 위의 파라미터들로 인제니코 api 수행함.
        try {
            Client client = getClient();
            try {
                ApprovePaymentNonSepaDirectDebitPaymentMethodSpecificInput directDebitPaymentMethodSpecificInput = new ApprovePaymentNonSepaDirectDebitPaymentMethodSpecificInput();
                directDebitPaymentMethodSpecificInput.setDateCollect("20150201");
                directDebitPaymentMethodSpecificInput.setToken("bfa8a7e4-4530-455a-858d-204ba2afb77e");

                OrderReferencesApprovePayment references = new OrderReferencesApprovePayment();
                references.setMerchantReference("AcmeOrder0001");

                OrderApprovePayment order = new OrderApprovePayment();
                order.setReferences(references);

                ApprovePaymentRequest body = new ApprovePaymentRequest();
                body.setAmount(2980L);
                body.setDirectDebitPaymentMethodSpecificInput(directDebitPaymentMethodSpecificInput);
                body.setOrder(order);

                PaymentApprovalResponse response = client.merchant("merchantId").payments().approve("paymentId", body);
            } finally {
                client.close();
            }
        } catch (Exception ex) {

        }

        //2단계. 인제니코 수행 후 리턴값(에러 등등) 으로 PaymentTransactionInfoPlugin 정보를 꾸며서 던져줌.

        System.out.println();
        return null;
    }

    private Client getClient() throws URISyntaxException {
        String apiKeyId = System.getProperty("connect.api.apiKeyId", "someKey");
        String secretApiKey = System.getProperty("connect.api.secretApiKey", "someSecret");

        URL propertiesUrl = getClass().getResource("/example-configuration.properties");
        CommunicatorConfiguration configuration = Factory.createConfiguration(propertiesUrl.toURI(), apiKeyId, secretApiKey);
        return Factory.createClient(configuration);
    }

    @Override
    public PaymentTransactionInfoPlugin capturePayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public PaymentTransactionInfoPlugin purchasePayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public PaymentTransactionInfoPlugin voidPayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public PaymentTransactionInfoPlugin creditPayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public PaymentTransactionInfoPlugin refundPayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public List<PaymentTransactionInfoPlugin> getPaymentInfo(UUID kbAccountId, UUID kbPaymentId, Iterable<PluginProperty> properties, TenantContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public Pagination<PaymentTransactionInfoPlugin> searchPayments(String searchKey, Long offset, Long limit, Iterable<PluginProperty> properties, TenantContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public void addPaymentMethod(UUID kbAccountId, UUID kbPaymentMethodId, PaymentMethodPlugin paymentMethodProps, boolean setDefault, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {

        System.out.println("kbAccountId : " + kbAccountId);
        System.out.println("kbPaymentMethodId : " + kbPaymentMethodId.toString());
        System.out.println("paymentMethodProps : " + paymentMethodProps.toString());

        //
        String token = null;
        List<PluginProperty> propertyList = paymentMethodProps.getProperties();
        for (PluginProperty pluginProperty : propertyList) {
            if (pluginProperty.getKey().equals("token")) {
                token = (String) pluginProperty.getValue();
            }
        }

    }

    @Override
    public void deletePaymentMethod(UUID kbAccountId, UUID kbPaymentMethodId, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {

    }

    @Override
    public PaymentMethodPlugin getPaymentMethodDetail(UUID kbAccountId, UUID kbPaymentMethodId, Iterable<PluginProperty> properties, TenantContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public void setDefaultPaymentMethod(UUID kbAccountId, UUID kbPaymentMethodId, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {

    }

    @Override
    public List<PaymentMethodInfoPlugin> getPaymentMethods(UUID kbAccountId, boolean refreshFromGateway, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public Pagination<PaymentMethodPlugin> searchPaymentMethods(String searchKey, Long offset, Long limit, Iterable<PluginProperty> properties, TenantContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public void resetPaymentMethods(UUID kbAccountId, List<PaymentMethodInfoPlugin> paymentMethods, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {

    }

    @Override
    public HostedPaymentPageFormDescriptor buildFormDescriptor(UUID kbAccountId, Iterable<PluginProperty> customFields, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public GatewayNotification processNotification(String notification, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }
}
