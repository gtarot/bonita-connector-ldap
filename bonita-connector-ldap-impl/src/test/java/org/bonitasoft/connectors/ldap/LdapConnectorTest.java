/**
 * Copyright (C) 2006  Bull S. A. S.
 * Bull, Rue Jean Jaures, B.P.68, 78340, Les Clayes-sous-Bois
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA  02110-1301, USA.
 **/
package org.bonitasoft.connectors.ldap;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.exception.BonitaException;

public class LdapConnectorTest extends TestCase {

    private LdapConnector connector;
    protected static final Logger LOG = Logger.getLogger(LdapConnectorTest.class.getName());

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (LdapConnectorTest.LOG.isLoggable(Level.WARNING)) {
            LdapConnectorTest.LOG.warning("======== Starting test: " + this.getClass().getName() + "." + this.getName() + "() ==========");
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if (LdapConnectorTest.LOG.isLoggable(Level.WARNING)) {
            LdapConnectorTest.LOG.warning("======== Ending test: " + this.getName() + " ==========");
        }
        connector = null;
        super.tearDown();
    }


    private void execute() {
        try {
            connector.execute();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Impossible: The execution should not throw any exception");
        }
    }

    private void failServiceUnavailableExecute() {
        try {
            connector.execute();
            fail("ServiceUnavailableException should have been thrown");
        } catch (ConnectorException e) {
        }
    }

    private LdapConnector getBasicSettings() {
        connector = new LdapConnector();
        connector.setHost("localhost");
        connector.setProtocol(LdapProtocol.LDAP);
        connector.setBaseObject("ou=people,dc=bonita,dc=org");
        connector.setFilter("(cn=*o*)");
        return connector;
    }

    private LdapConnector getDirectoryServiceSettings() {
        connector = new LdapConnector();
        connector.setHost("192.168.1.212");
        connector.setPort(10389);
        connector.setProtocol(LdapProtocol.LDAP);
        connector.setUserName("cn=Directory Manager");
        connector.setPassword("bonita-secret");
        connector.setBaseObject("ou=people,dc=bonita,dc=org");
        connector.setScope(LdapScope.ONELEVEL);
        connector.setFilter("(cn=*o*)");
        return connector;
    }

    private LdapConnector getSSLDirectoryServiceSettings() {
        connector = new LdapConnector();
        connector.setHost("192.168.1.212");
        connector.setPort(10636);
        connector.setProtocol(LdapProtocol.LDAPS);
        connector.setUserName("cn=Directory Manager");
        connector.setPassword("bonita-secret");
        connector.setBaseObject("ou=people,dc=bonita,dc=org");
        connector.setScope(LdapScope.ONELEVEL);
        connector.setFilter("(cn=*o*)");
        return connector;
    }

    public void testValidate() {
        connector = new LdapConnector();
        try {
            connector.validateInputParameters();
            fail("Should get 4 errors");
        } catch (ConnectorValidationException e) {

        }

        connector.setHost("mylocalhost");
        try {
            connector.validateInputParameters();
            fail("Should get 3 errors");
        } catch (ConnectorValidationException e) {

        }

        connector.setProtocol(LdapProtocol.LDAP);
        try {
            connector.validateInputParameters();
            fail("Should get 2 errors");
        } catch (ConnectorValidationException e) {

        }

        connector.setBaseObject("dc=mydomain,dc=com");
        try {
            connector.validateInputParameters();
            fail("Should get 1 error");
        } catch (ConnectorValidationException e) {
        }

        connector.setFilter("(cn=Tom*)");
        // All required fields were set
        try {
            connector.validateInputParameters();
        } catch (ConnectorValidationException e) {
            fail("Should not fail as all parameters are set");
        }

        connector.setUserName("What I want");
        connector.setPassword(null);
        try {
            connector.validateInputParameters();
            fail("Should get 1 error");
        } catch (ConnectorValidationException e) {
        }

        connector.setUserName(null);
        connector.setPassword("What I want");
        try {
            connector.validateInputParameters();
            fail("Should get 1 error");
        } catch (ConnectorValidationException e) {
        }

        connector.setUserName("");
        connector.setPassword("");
        try {
            connector.validateInputParameters();
        } catch (ConnectorValidationException e) {
            fail("Should not fail as all parameters are set");
        }

        connector.setUserName("What I want");
        connector.setPassword("What I want");

        try {
            connector.validateInputParameters();
        } catch (ConnectorValidationException e) {
            fail("Should not fail as all parameters are set");
        }

        connector.setUserName("What I want");
        connector.setPassword("pwd");
        try {
            connector.validateInputParameters();
        } catch (ConnectorValidationException e) {
            fail("Should not fail as all parameters are set");
        }
    }

    public void testValidateValues() throws ConnectorValidationException {
        connector = getBasicSettings();
        connector = getDirectoryServiceSettings();
        connector.validateInputParameters();
    }

    public void testSetNullHost() {
        connector = getBasicSettings();
        connector.setHost(null);
        try {
            connector.validateInputParameters();
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("host"));
        }
    }

    public void testSetEmptyHost() throws BonitaException {
        connector = getBasicSettings();
        connector.setHost("");
        try {
            connector.validateInputParameters();
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("host"));
        }
    }

    public void testSetPortWithLessThanRange() throws BonitaException {
        connector = getBasicSettings();
        connector.setPort(-1);

        try {
            connector.validateInputParameters();
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("port"));
        }
    }

    public void testSetPortWithGreaterThanRange() throws BonitaException {
        connector = getBasicSettings();
        connector.setPort(65536);

        try {
            connector.validateInputParameters();
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("port"));
        }
    }

    public void testSetNullPort() throws BonitaException {
        connector = getBasicSettings();
        connector.setPort(null);

        try {
            connector.validateInputParameters();
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("port"));
        }
    }

    public void testSetProtocol() {
        connector = getBasicSettings();
        connector.setProtocol("LDAP");
        assertEquals(LdapProtocol.LDAP, connector.getProtocol());
        connector.setProtocol("LDAPS");
        assertEquals(LdapProtocol.LDAPS, connector.getProtocol());
        connector.setProtocol("TLS");
        assertEquals(LdapProtocol.TLS, connector.getProtocol());

        connector.setProtocol("ldap");
        assertEquals(LdapProtocol.LDAP, connector.getProtocol());
        connector.setProtocol("ldaps");
        assertEquals(LdapProtocol.LDAPS, connector.getProtocol());
        connector.setProtocol("tls");
        assertEquals(LdapProtocol.TLS, connector.getProtocol());
    }

    public void testSetNullProtocol() throws BonitaException {
        connector = getBasicSettings();
        LdapProtocol protocol = null;
        connector.setProtocol(protocol);

        try {
            connector.validateInputParameters();
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("protocol"));
        }
    }

    public void testSetNullStringProtocol() throws ConnectorValidationException {
        connector = getBasicSettings();
        String protocol = null;
        connector.setProtocol(protocol);
        connector.validateInputParameters();

        assertEquals(LdapProtocol.LDAPS, connector.getProtocol());
    }

    public void testSetBadProtocol() throws BonitaException {
        connector = getBasicSettings();
        connector.setProtocol("HTTP");

        connector.validateInputParameters();
        assertEquals(LdapProtocol.LDAPS, connector.getProtocol());
    }

    public void testSetBaseObject() {
        connector = getBasicSettings();
        connector.setBaseObject(null);

        try {
            connector.validateInputParameters();
            fail("Should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("baseObject"));
        }
    }

    public void testSetScope() {
        connector = new LdapConnector();
        connector.setScope("BASE");
        assertEquals(LdapScope.BASE, connector.getScope());
        connector.setScope("ONELEVEL");
        assertEquals(LdapScope.ONELEVEL, connector.getScope());
        connector.setScope("SUBTREE");
        assertEquals(LdapScope.SUBTREE, connector.getScope());

        connector.setScope("base");
        assertEquals(LdapScope.BASE, connector.getScope());
        connector.setScope("onelevel");
        assertEquals(LdapScope.ONELEVEL, connector.getScope());
        connector.setScope("subtree");
        assertEquals(LdapScope.SUBTREE, connector.getScope());
    }

    public void testSetBadScope() throws BonitaException {
        connector = getBasicSettings();
        connector.setScope("ALLTREE");

        connector.validateInputParameters();
        assertEquals(LdapScope.ONELEVEL, connector.getScope());
    }

    public void testSetNullScope() {
        connector = getBasicSettings();
        LdapScope scope = null;
        connector.setScope(scope);
        try {
            connector.validateInputParameters();
            fail("Should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("scope"));
        }
    }

    public void testSetNullStringScope() throws ConnectorValidationException {
        connector = getBasicSettings();
        String scope = null;
        connector.setScope(scope);
        connector.validateInputParameters();
        assertEquals(LdapScope.ONELEVEL, connector.getScope());
    }

    public void testSetNullAttributes() throws ConnectorValidationException {
        connector = getBasicSettings();
        String[] attribs = null;
        connector.setAttributes(attribs);
        connector.validateInputParameters();
    }

    public void testSetNullSizeLimit() {
        connector = getBasicSettings();
        connector.setSizeLimit(null);
        try {
            connector.validateInputParameters();
            fail("Should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("sizeLimit"));
        }
    }

    public void testSetNegativeLongSizeLimit() throws BonitaException {
        connector = getBasicSettings();
        connector.setSizeLimit(new Long(-1L));

        try {
            connector.validateInputParameters();
            fail("Should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("sizeLimit"));
        }
    }

    public void testSetNegativeSizeLimit() throws BonitaException {
        connector = getBasicSettings();
        connector.setSizeLimit(-1L);

        try {
            connector.validateInputParameters();
            fail("Should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("sizeLimit"));
        }
    }

    public void testSetNullFilter() throws BonitaException {
        connector = getBasicSettings();
        connector.setFilter(null);

        try {
            connector.validateInputParameters();
            fail("Should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("filter"));
        }
    }

    public void testSearchAUserWithAllAttributes() {
        connector = getDirectoryServiceSettings();
        connector.setFilter("(cn=John D*)");
        execute();

        List<List<LdapAttribute>> list = connector.getLdapAttributeList();
        assertEquals(1, list.size());

        LdapAttribute email = new LdapAttribute("mail", "john.doe@bonita.org");
        LdapAttribute cn = new LdapAttribute("cn", "John Doe");
        LdapAttribute uid = new LdapAttribute("uid", "doej");
        LdapAttribute person = new LdapAttribute("objectClass", "person");
        LdapAttribute org = new LdapAttribute("objectClass",
                "organizationalPerson");
        LdapAttribute inet = new LdapAttribute("objectClass", "inetOrgPerson");
        LdapAttribute top = new LdapAttribute("objectClass", "top");
        LdapAttribute sn = new LdapAttribute("sn", "Doe");
        List<LdapAttribute> attributes =list.get(0);
        assertEquals(9, attributes.size());
        assertTrue(attributes.contains(sn));
        assertTrue(attributes.contains(email));
        assertTrue(attributes.contains(uid));
        assertTrue(attributes.contains(cn));
        assertTrue(attributes.contains(inet));
        assertTrue(attributes.contains(person));
        assertTrue(attributes.contains(org));
        assertTrue(attributes.contains(top));
        // Do not check password because it is only a reference
        // to the real password, it changes depending on the configuration
    }

    public void testSearchUsersWithSomeEntryAttributes() {
        connector = getDirectoryServiceSettings();
        connector.setAttributes(new String[]{"cn", "mail"});
        connector.setFilter("(|(cn=John D*)(cn=Pierre*))");
        execute();
        List<List<LdapAttribute>> list = connector.getLdapAttributeList();
        assertEquals(2, list.size());

        LdapAttribute johnEmail = new LdapAttribute("mail", "john.doe@bonita.org");
        LdapAttribute john = new LdapAttribute("cn", "John Doe");
        LdapAttribute pierre = new LdapAttribute("cn", "Pierre Dupont");
        LdapAttribute pierreEmail = new LdapAttribute(
                "mail", "pierre.dupont@bonita.org");
        for (List<LdapAttribute> attributes : list) {
            assertEquals(2, attributes.size());
            assertTrue((attributes.contains(john) && attributes.contains(johnEmail))
                    || (attributes.contains(pierre) && attributes.contains(pierreEmail)));
        }
    }

    public void testSearchUsersWithSizeLimit() {
        connector = getDirectoryServiceSettings();
        connector.setFilter("(!(cn=Matt*))");
        execute();
        assertEquals(5, connector.getLdapAttributeList().size());

        connector.setSizeLimit(3);
        execute();
        assertEquals(3, connector.getLdapAttributeList().size());

        connector.setSizeLimit(new Long(3L));
        execute();
        assertEquals(3, connector.getLdapAttributeList().size());

        connector.setSizeLimit(3L);
        execute();
        assertEquals(3, connector.getLdapAttributeList().size());
    }

    public void testSearchUsersWithSizeLimitMoreThanResults() {
        connector = getDirectoryServiceSettings();
        connector.setFilter("(|(cn=John D*)(cn=Pierre*))");
        execute();
        assertEquals(2, connector.getLdapAttributeList().size());

        connector.setSizeLimit(10);
        execute();
        assertEquals(2, connector.getLdapAttributeList().size());
    }

    public void testSearchUsersWithAnonymousAccess() {
        connector = getDirectoryServiceSettings();
        connector.setUserName(null);
        connector.setPassword(null);
        connector.setFilter("(|(cn=John D*)(cn=Pierre*))");
        execute();
        assertEquals(2, connector.getLdapAttributeList().size());
    }

    public void testGroupUsersSearch() {
        connector = getDirectoryServiceSettings();
        connector.setBaseObject("ou=groups,dc=bonita,dc=org");
        connector.setFilter("(cn=B-team)");
        connector.setAttributes(new String[] {"uniqueMember"});
        connector.setScope(LdapScope.SUBTREE);
        execute();
        List<List<LdapAttribute>> list = connector.getLdapAttributeList();
        assertEquals(1, list.size());
        List<LdapAttribute> attributes = list.get(0);
        LdapAttribute john = new LdapAttribute("uniqueMember",
                "cn=John Doe,ou=people,dc=bonita,dc=org");
        LdapAttribute pierre = new LdapAttribute("uniqueMember",
                "cn=Pierre Dupont,ou=people,dc=bonita,dc=org");
        assertTrue(attributes.contains(john));
        assertTrue(attributes.contains(pierre));
    }

    public void testGetEmailFromUserID() {
        connector = getDirectoryServiceSettings();
        connector.setBaseObject("ou=people,dc=bonita,dc=org");
        connector.setFilter("(uid=doej)");
        connector.setAttributes(new String[] {"mail"});
        connector.setScope(LdapScope.SUBTREE);
        execute();
        assertEquals(1, connector.getLdapAttributeList().size());
        LdapAttribute expected = new LdapAttribute("mail", "john.doe@bonita.org");
        LdapAttribute actual = connector.getLdapAttributeList().get(0).get(0);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue());
    }

    public void testSearchOnAnLDAPSDirectory() {
        connector = getSSLDirectoryServiceSettings();
        connector.setProtocol(LdapProtocol.LDAP);
        failServiceUnavailableExecute();
    }

    public void testSearchWithSSLSupportOnAnLDAPDirectory() {
        connector = getDirectoryServiceSettings();
        connector.setProtocol(LdapProtocol.LDAPS);
        try {
            connector.execute();
            fail("CommunicationException should have been thrown");
        } catch (ConnectorException e) {

        }
    }

    public void testSearchWithSSLSupportOnAnLDAPDirectoryUsingTLS() throws Exception {
        connector = getSSLDirectoryServiceSettings();
        connector.setProtocol(LdapProtocol.TLS);
        failServiceUnavailableExecute();
    }

    public void testFailAuthenticationBadPassword() {
        connector = getDirectoryServiceSettings();
        connector.setPassword("badPassword");
        try {
            connector.execute();
            fail("AuthenticationException should have been thrown");
        } catch (ConnectorException e) {
        }
    }

    public void testFailAuthenticationBadUserName() {
        connector = getDirectoryServiceSettings();
        connector.setUserName("cn=Directory User");
        try {
            connector.execute();
            fail("AuthenticationException should have been thrown");
        } catch (ConnectorException e) {

        }
    }

    public void testSetTimeLimitWithANegativeValue() {
        connector = getBasicSettings();
        connector.setTimeLimit(-4);

        try {
            connector.validateInputParameters();
            fail("Should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("timeLimit"));
        }
    }

    public void testSetNullTimeLimit() throws BonitaException {
        connector = getBasicSettings();
        connector.setTimeLimit(null);

        try {
            connector.validateInputParameters();
            fail("Should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("timeLimit"));
        }
    }

    public void testSetNullReferralHandling() throws BonitaException {
        connector = getBasicSettings();
        connector.setReferralHandling(null);

        try {
            connector.validateInputParameters();
            fail("Should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("referralHandling"));
        }
    }

    public void testSetBadReferralHandling() {
        connector = getBasicSettings();
        connector.setReferralHandling("always");

        try {
            connector.validateInputParameters();
            fail("Should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("referralHandling"));
        }

        connector = getBasicSettings();
        connector.setReferralHandling("IGNORE");

        try {
            connector.validateInputParameters();
            fail("Should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("referralHandling"));
        }

        connector = getBasicSettings();
        connector.setReferralHandling("FOLLOW");
        try {
            connector.validateInputParameters();
            fail("Should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("referralHandling"));
        }
    }

    public void testSetReferralHandling() throws BonitaException {
        connector = getBasicSettings();
        connector.setReferralHandling("ignore");
        connector.validateInputParameters();
        connector.setReferralHandling("follow");
        connector.validateInputParameters();
    }

    public void testSearchUsersFollowingRefferal() {
        connector = getDirectoryServiceSettings();
        connector.setBaseObject("ou=people,dc=bonita,dc=org");
        connector.setFilter("(uid=doej)");
        connector.setAttributes(new String[] {"mail"});
        connector.setScope(LdapScope.SUBTREE);
        execute();
        assertEquals(1, connector.getLdapAttributeList().size());
        LdapAttribute expected = new LdapAttribute("mail", "john.doe@bonita.org");
        LdapAttribute actual = connector.getLdapAttributeList().get(0).get(0);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue());
    }

    public void testUserPasswordAttribute() throws Exception {
        connector = getDirectoryServiceSettings();
        connector.setFilter("(uid=doej)");
        connector.setScope(LdapScope.SUBTREE);
        connector.setAttributes(new String[] { "userPassword" });
        connector.execute();
        List<List<LdapAttribute>> attributeList = connector.getLdapAttributeList();
        assertEquals(1, attributeList.size());
        List<LdapAttribute> attributes = attributeList.get(0);
        assertEquals(1, attributes.size());
        LdapAttribute password = attributes.get(0);
        assertEquals("userPassword", password.getName());
        assertEquals("{SSHA}T+zTRpbU3JlZpSLvcaGJcWlPVaUbwTAenqJSTA==", password.getValue());
    }

  /*public void testGetEmailFromUserIDWithSSLSupport() {
    connector = getSSLDirectoryServiceSettings();
    connector.setBaseObject("ou=people,dc=bonita,dc=org");
    connector.setFilter("(uid=doej)");
    connector.setAttributes(new String[] {"mail"});
    connector.setScope(LdapScope.SUBTREE);
    execute();
    assertEquals(1, connector.getLdapAttributeList().size());
    LdapAttribute expected = new LdapAttribute("mail", "john.doe@bonita.org");
    LdapAttribute actual = connector.getLdapAttributeList().get(0).get(0);
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getValue(), actual.getValue());
  }

  public void testGetEmailFromUserIDWithSSLSupportAndCertificate() throws Exception {
    connector = getSSLDirectoryServiceSettings();
    connector.setBaseObject("ou=people,dc=bonita,dc=org");
    connector.setFilter("(uid=doej)");
    connector.setAttributes(new String[] {"mail"});
    connector.setScope(LdapScope.SUBTREE);
    connector.setCertificatePath("E:\\truststore");
    connector.executeConnector();
    assertEquals(25, connector.getResult().length());
    String result = "mail" + Connector.KEY_VALUE_SEPARATOR
    + "john.doe@bonita.org";
    assertEquals(result, connector.getResult());
  }

  public void testGetEmailFromUserIDWithTLSSupport() {
    connector = getDirectoryServiceSettings();
    connector.setBaseObject("ou=people,dc=bonita,dc=org");
    connector.setFilter("(uid=doej)");
    connector.setProtocol(LdapProtocol.TLS);
    connector.setAttributes(new String[] {"mail"});
    connector.setScope(LdapScope.SUBTREE);
    execute();
    assertEquals(1, connector.getLdapAttributeList().size());
    LdapAttribute expected = new LdapAttribute("mail", "john.doe@bonita.org");
    LdapAttribute actual = connector.getLdapAttributeList().get(0).get(0);
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getValue(), actual.getValue());
  }

  public void testSearchWithAliasDereferencing() {
    //TODO
  }*/
}
