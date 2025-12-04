import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * Test cases for the DonationFiler class
 */
class DonationFilerTest {
    private static final String TEST_FILE = "test_donations.csv";
    private DonationFiler filer;

    @BeforeEach
    void setUp() {
        deleteTestFile();
        filer = new DonationFiler(TEST_FILE);
    }

    @AfterEach
    void tearDown() {
        deleteTestFile();
    }

    @Test
    @DisplayName("File creation path - file doesn't exist")
    void testFileCreationWhenMissing() {
        deleteTestFile();
        DonationFiler newFiler = new DonationFiler(TEST_FILE);
        File f = new File(TEST_FILE);
        assertTrue(f.exists(), "File should be created if missing");
    }

    @Test
    @DisplayName("File creation path - file already exists")
    void testFileCreationWhenExists() throws IOException {
        // Pre-create the file
        new FileWriter(TEST_FILE).close();
        DonationFiler newFiler = new DonationFiler(TEST_FILE);
        File f = new File(TEST_FILE);
        assertTrue(f.exists(), "Existing file should remain");
    }

    @Test
    @DisplayName("Append - success path")
    void testAppendSuccessPath() {
        Donation d = new Donation("Alice", 50.0);
        filer.append(d);
        List<Donation> loaded = filer.loadAll();
        assertEquals(1, loaded.size());
        assertEquals("Alice", loaded.get(0).getName());
    }

    @Test
    @DisplayName("LoadAll - empty file path")
    void testLoadAllEmptyFile() {
        List<Donation> donations = filer.loadAll();
        assertTrue(donations.isEmpty(), "Empty file should return empty list");
    }

    @Test
    @DisplayName("LoadAll - file with valid donations")
    void testLoadAllWithValidDonations() {
        filer.append(new Donation("Bob", 100.0));
        filer.append(new Donation("Carol", 200.0));
        List<Donation> donations = filer.loadAll();
        assertEquals(2, donations.size());
    }

    private void deleteTestFile() {
        try {
            Files.deleteIfExists(Paths.get(TEST_FILE));
        } catch (IOException e) {
        }
    }

}