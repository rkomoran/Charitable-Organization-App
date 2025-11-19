
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

/**
 * Test cases for the Donation class
 */
class DonationTest {

    @Test
    @DisplayName("Create valid donation with positive amount")
    void testValidDonation() {
        Donation donation = new Donation("John Doe", 50.0);

        assertEquals("John Doe", donation.getName());
        assertEquals(50.0, donation.getAmount(), 0.001);
    }

    @Test
    @DisplayName("Create donation with zero amount")
    void testZeroAmountDonation() {
        Donation donation = new Donation("Jane Smith", 0.0);

        assertEquals("Jane Smith", donation.getName());
        assertEquals(0.0, donation.getAmount(), 0.001);
    }

    @Test
    @DisplayName("Reject negative donation amount")
    void testNegativeDonation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Donation("Bob", -10.0);
        });

        assertTrue(exception.getMessage().contains("cannot be negative"));
    }

    @Test
    @DisplayName("Convert donation to CSV string format")
    void testToString() {
        Donation donation = new Donation("Alice", 100.50);

        assertEquals("Alice,100.5", donation.toString());
    }

    @Test
    @DisplayName("Parse donation from CSV string")
    void testFromString() {
        String csvLine = "John Doe,75.25";
        Donation donation = Donation.fromString(csvLine);

        assertNotNull(donation);
        assertEquals("John Doe", donation.getName());
        assertEquals(75.25, donation.getAmount(), 0.001);
    }

    @Test
    @DisplayName("Handle donation with name containing special characters")
    void testSpecialCharactersInName() {
        Donation donation = new Donation("O'Brien-Smith", 25.0);

        assertEquals("O'Brien-Smith", donation.getName());
        assertEquals(25.0, donation.getAmount(), 0.001);
    }

    @Test
    @DisplayName("Handle large donation amounts")
    void testLargeDonation() {
        Donation donation = new Donation("Big Donor", 999999.99);

        assertEquals(999999.99, donation.getAmount(), 0.001);
    }

    @Test
    @DisplayName("Parse donation with decimal amount from string")
    void testFromStringWithDecimal() {
        Donation donation = Donation.fromString("Test User,123.456");

        assertEquals("Test User", donation.getName());
        assertEquals(123.456, donation.getAmount(), 0.001);
    }
}