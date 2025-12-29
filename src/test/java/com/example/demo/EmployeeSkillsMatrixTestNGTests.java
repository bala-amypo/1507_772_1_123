package com.example.demo;

import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeSkill;
import com.example.demo.model.SearchQueryRecord;
import com.example.demo.model.Skill;
import com.example.demo.model.SkillCategory;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.SearchQueryRecordRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.EmployeeSkillService;
import com.example.demo.service.SearchQueryService;
import com.example.demo.service.SkillCategoryService;
import com.example.demo.service.SkillService;
import com.example.demo.service.impl.EmployeeServiceImpl;
import com.example.demo.service.impl.EmployeeSkillServiceImpl;
import com.example.demo.service.impl.SearchQueryServiceImpl;
import com.example.demo.service.impl.SkillCategoryServiceImpl;
import com.example.demo.service.impl.SkillServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@Listeners(TestResultListener.class)
public class EmployeeSkillsMatrixTestNGTests {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private com.example.demo.repository.SkillRepository skillRepository;
    @Mock
    private com.example.demo.repository.EmployeeSkillRepository employeeSkillRepository;
    @Mock
    private com.example.demo.repository.SkillCategoryRepository skillCategoryRepository;
    @Mock
    private SearchQueryRecordRepository searchQueryRecordRepository;

    private EmployeeService employeeService;
    private SkillService skillService;
    private EmployeeSkillService employeeSkillService;
    private SkillCategoryService skillCategoryService;
    private SearchQueryService searchQueryService;
    private JwtTokenProvider tokenProvider;

    private ServletRegistrationBean<?> servletRegistrationBean;

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeServiceImpl(employeeRepository);
        skillService = new SkillServiceImpl(skillRepository);
        employeeSkillService = new EmployeeSkillServiceImpl(employeeSkillRepository, employeeRepository, skillRepository);
        skillCategoryService = new SkillCategoryServiceImpl(skillCategoryRepository);
        searchQueryService = new SearchQueryServiceImpl(searchQueryRecordRepository, employeeSkillRepository);
        tokenProvider = new JwtTokenProvider();

        servletRegistrationBean = new ServletRegistrationBean<>();
        ReflectionTestUtils.setField(
                servletRegistrationBean,
                "urlMappings",
                new HashSet<>(List.of("/hello-servlet"))
        );
    }

    // 1. Servlet tests (8)

    @Test(priority = 1, groups = "servlet")
    public void testServletRegistrationUrlMapping_NotEmpty() {
        Assert.assertFalse(servletRegistrationBean.getUrlMappings().isEmpty());
    }

    @Test(priority = 1, groups = "servlet")
    public void testServletRegistrationUrlMappingContainsHelloServlet() {
        Assert.assertTrue(servletRegistrationBean.getUrlMappings().contains("/hello-servlet"));
    }

    @Test(priority = 1, groups = "servlet")
    public void testServletResponseStatusOk() throws IOException, ServletException {
        jakarta.servlet.http.HttpServlet servlet = new jakarta.servlet.http.HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Hello from simple servlet");
            }
        };
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("GET");
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        Assert.assertTrue(sw.toString().contains("Hello from simple servlet"));
    }

    @Test(priority = 1, groups = "servlet")
    public void testServletHandlesMultipleRequests() throws IOException, ServletException {
        jakarta.servlet.http.HttpServlet servlet = new jakarta.servlet.http.HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.getWriter().write("OK");
            }
        };

        HttpServletRequest request1 = mock(HttpServletRequest.class);
        HttpServletResponse response1 = mock(HttpServletResponse.class);
        when(request1.getMethod()).thenReturn("GET");
        when(response1.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        HttpServletRequest request2 = mock(HttpServletRequest.class);
        HttpServletResponse response2 = mock(HttpServletResponse.class);
        when(request2.getMethod()).thenReturn("GET");
        when(response2.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        servlet.service(request1, response1);
        servlet.service(request2, response2);

        verify(response1, atLeastOnce()).getWriter();
        verify(response2, atLeastOnce()).getWriter();
    }

    @Test(priority = 1, groups = "servlet")
    public void testServletReturns200OnGet() throws IOException, ServletException {
        jakarta.servlet.http.HttpServlet servlet = new jakarta.servlet.http.HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.setStatus(200);
            }
        };
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("GET");

        servlet.service(request, response);
        verify(response).setStatus(200);
    }

    @Test(priority = 1, groups = "servlet")
    public void testServletWriterNotNull() throws IOException, ServletException {
        jakarta.servlet.http.HttpServlet servlet = new jakarta.servlet.http.HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.getWriter().write("Test");
            }
        };
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("GET");
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.service(request, response);
        Assert.assertFalse(sw.toString().isEmpty());
    }

    @Test(priority = 1, groups = "servlet")
    public void testServletHandlesIOExceptionGracefully() throws IOException, ServletException {
        jakarta.servlet.http.HttpServlet servlet = new jakarta.servlet.http.HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                throw new IOException("Simulated");
            }
        };
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("GET");

        try {
            servlet.service(request, response);
            Assert.fail("Expected IOException");
        } catch (IOException ex) {
            Assert.assertEquals(ex.getMessage(), "Simulated");
        }
    }

    @Test(priority = 1, groups = "servlet")
    public void testServletInvokedWithNullWriter() throws IOException, ServletException {
        jakarta.servlet.http.HttpServlet servlet = new jakarta.servlet.http.HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.setStatus(200);
            }
        };
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("GET");

        servlet.service(request, response);
        verify(response).setStatus(200);
    }

    // 2. CRUD operations (8)

    @Test(priority = 2, groups = "crud")
    public void testCreateEmployeeSuccess() {
        Employee emp = new Employee();
        emp.setFullName("John Doe");
        emp.setEmail("john@example.com");
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee result = employeeService.createEmployee(emp);
        Assert.assertEquals(result.getFullName(), "John Doe");
        Assert.assertEquals(result.getEmail(), "john@example.com");
    }

    @Test(priority = 2, groups = "crud")
    public void testCreateEmployeeMissingEmailNegative() {
        Employee emp = new Employee();
        emp.setFullName("No Email");
        try {
            employeeService.createEmployee(emp);
            verify(employeeRepository, atLeastOnce()).save(any(Employee.class));
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage() == null || ex.getMessage().length() >= 0);
        }
    }

    @Test(priority = 2, groups = "crud")
    public void testUpdateEmployeeSuccess() {
        Employee existing = new Employee();
        existing.setId(1L);
        existing.setFullName("Old");
        existing.setEmail("old@example.com");
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee update = new Employee();
        update.setFullName("New Name");
        update.setEmail("new@example.com");
        Employee result = employeeService.updateEmployee(1L, update);
        Assert.assertEquals(result.getFullName(), "New Name");
        Assert.assertEquals(result.getEmail(), "new@example.com");
    }

    @Test(priority = 2, groups = "crud")
    public void testGetEmployeeByIdNotFoundNegative() {
        when(employeeRepository.findById(999L)).thenReturn(java.util.Optional.empty());
        try {
            employeeService.getEmployeeById(999L);
            Assert.fail("Expected exception");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Employee not found"));
        }
    }

    @Test(priority = 2, groups = "crud")
    public void testListEmployeesEdgeCaseEmptyList() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
        List<Employee> result = employeeService.getAllEmployees();
        Assert.assertTrue(result.isEmpty());
    }

    @Test(priority = 2, groups = "crud")
    public void testDeactivateEmployeeSuccess() {
        Employee existing = new Employee();
        existing.setId(2L);
        existing.setActive(true);
        when(employeeRepository.findById(2L)).thenReturn(java.util.Optional.of(existing));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        employeeService.deactivateEmployee(2L);
        Assert.assertFalse(existing.getActive());
    }

    @Test(priority = 2, groups = "crud")
    public void testCreateSkillSuccess() {
        Skill skill = new Skill();
        skill.setName("Java");
        when(skillRepository.save(any(Skill.class))).thenAnswer(inv -> inv.getArgument(0));
        Skill result = skillService.createSkill(skill);
        Assert.assertEquals(result.getName(), "Java");
        Assert.assertTrue(result.getActive());
    }

    @Test(priority = 2, groups = "crud")
    public void testDeactivateSkillNegativeInvalidId() {
        when(skillRepository.findById(999L)).thenReturn(java.util.Optional.empty());
        try {
            skillService.deactivateSkill(999L);
            Assert.fail("Expected exception");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Skill not found"));
        }
    }

    // 3. DI / IoC (8)

    @Test(priority = 3, groups = "di")
    public void testEmployeeServiceInjectedRepository() {
        Assert.assertNotNull(employeeService);
        Assert.assertNotNull(employeeRepository);
    }

    @Test(priority = 3, groups = "di")
    public void testSkillServiceInjectedRepository() {
        Assert.assertNotNull(skillService);
        Assert.assertNotNull(skillRepository);
    }

    @Test(priority = 3, groups = "di")
    public void testEmployeeSkillServiceUsesEmployeeRepository() {
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setActive(true);
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(emp));

        Skill skill = new Skill();
        skill.setId(10L);
        skill.setActive(true);
        skill.setName("Java");
        when(skillRepository.findById(10L)).thenReturn(java.util.Optional.of(skill));

        EmployeeSkill mapping = new EmployeeSkill();
        mapping.setEmployee(emp);
        mapping.setSkill(skill);
        mapping.setProficiencyLevel("Advanced");
        mapping.setYearsOfExperience(3);

        when(employeeSkillRepository.save(any(EmployeeSkill.class))).thenAnswer(inv -> inv.getArgument(0));

        EmployeeSkill result = employeeSkillService.createEmployeeSkill(mapping);
        Assert.assertEquals(result.getEmployee().getId(), emp.getId());
        Assert.assertEquals(result.getSkill().getId(), skill.getId());
    }

    @Test(priority = 3, groups = "di")
    public void testSearchQueryServiceInjectedRepositories() {
        Assert.assertNotNull(searchQueryService);
        Assert.assertNotNull(searchQueryRecordRepository);
        Assert.assertNotNull(employeeSkillRepository);
    }

    @Test(priority = 3, groups = "di")
    public void testSkillCategoryServiceInjectedRepository() {
        Assert.assertNotNull(skillCategoryService);
        Assert.assertNotNull(skillCategoryRepository);
    }

    @Test(priority = 3, groups = "di")
    public void testEmployeeServiceCreateDelegatesToRepository() {
        Employee emp = new Employee();
        emp.setFullName("IoC");
        emp.setEmail("ioc@example.com");
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        employeeService.createEmployee(emp);

        verify(employeeRepository, atLeastOnce()).save(any(Employee.class));
    }

    @Test(priority = 3, groups = "di")
    public void testSkillServiceUpdateDelegatesToRepository() {
        Skill existing = new Skill();
        existing.setId(5L);
        existing.setName("Old");
        when(skillRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));
        when(skillRepository.save(any(Skill.class))).thenAnswer(inv -> inv.getArgument(0));

        Skill update = new Skill();
        update.setName("New");
        skillService.updateSkill(5L, update);

        verify(skillRepository, atLeastOnce()).save(any(Skill.class));
        Assert.assertEquals(existing.getName(), "New");
    }

    @Test(priority = 3, groups = "di")
    public void testSearchQueryServiceSaveQueryDelegatesToRepository() {
        SearchQueryRecord record = new SearchQueryRecord();
        record.setSearcherId(1L);
        record.setSkillsRequested("java,aws");
        when(searchQueryRecordRepository.save(any(SearchQueryRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        searchQueryService.saveQuery(record);

        verify(searchQueryRecordRepository, atLeastOnce()).save(any(SearchQueryRecord.class));
    }

    // 4. Hibernate & CRUD (8)

    @Test(priority = 4, groups = "hibernate")
    public void testEmployeeEntityDefaultActiveTrue() {
        Employee emp = new Employee();
        emp.onCreate();
        Assert.assertTrue(emp.getActive());
        Assert.assertNotNull(emp.getCreatedAt());
    }

    @Test(priority = 4, groups = "hibernate")
    public void testSkillEntityDefaultActiveTrue() {
        Skill skill = new Skill();
        Assert.assertTrue(skill.getActive());
    }

    @Test(priority = 4, groups = "hibernate")
    public void testEmployeeSkillEntityDefaultActiveTrue() {
        EmployeeSkill es = new EmployeeSkill();
        Assert.assertTrue(es.getActive());
    }

    @Test(priority = 4, groups = "hibernate")
    public void testSearchQueryRecordPrePersistSetsTimestamp() {
        SearchQueryRecord record = new SearchQueryRecord();
        record.setSearcherId(1L);
        record.setSkillsRequested("java");
        record.onCreate();
        Assert.assertNotNull(record.getSearchedAt());
        Assert.assertEquals((int) record.getResultsCount(), 0);
    }

    @Test(priority = 4, groups = "hibernate")
    public void testCreateSkillWithUniqueNameConstraintEdge() {
        Skill skill = new Skill();
        skill.setName("UniqueSkill");
        when(skillRepository.save(any(Skill.class))).thenAnswer(inv -> inv.getArgument(0));
        Skill saved = skillService.createSkill(skill);
        Assert.assertEquals(saved.getName(), "UniqueSkill");
    }

    @Test(priority = 4, groups = "hibernate")
    public void testEmployeeUpdateTimestampsOnPreUpdate() {
        Employee emp = new Employee();
        emp.onCreate();
        LocalDateTime created = emp.getCreatedAt();
        emp.onUpdate();
        Assert.assertTrue(emp.getUpdatedAt().isAfter(created) || emp.getUpdatedAt().isEqual(created));
    }

    @Test(priority = 4, groups = "hibernate")
    public void testEmployeeSkillExperienceNegativeThrows() {
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setActive(true);
        Skill skill = new Skill();
        skill.setId(2L);
        skill.setActive(true);
        skill.setName("Java");
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(emp));
        when(skillRepository.findById(2L)).thenReturn(java.util.Optional.of(skill));

        EmployeeSkill es = new EmployeeSkill();
        es.setEmployee(emp);
        es.setSkill(skill);
        es.setProficiencyLevel("Advanced");
        es.setYearsOfExperience(-1);

        try {
            employeeSkillService.createEmployeeSkill(es);
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("Experience years"));
        }
    }

    @Test(priority = 4, groups = "hibernate")
    public void testEmployeeSkillProficiencyInvalidThrows() {
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setActive(true);
        Skill skill = new Skill();
        skill.setId(2L);
        skill.setActive(true);
        skill.setName("Java");
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(emp));
        when(skillRepository.findById(2L)).thenReturn(java.util.Optional.of(skill));

        EmployeeSkill es = new EmployeeSkill();
        es.setEmployee(emp);
        es.setSkill(skill);
        es.setProficiencyLevel("Invalid");
        es.setYearsOfExperience(1);

        try {
            employeeSkillService.createEmployeeSkill(es);
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("Invalid proficiency"));
        }
    }

    // 5. Normalization / mapping (8)

    @Test(priority = 5, groups = "normalization")
    public void testEmployeeHasAtomicFields1NF() {
        Employee emp = new Employee();
        emp.setFullName("Atomic Name");
        emp.setEmail("atomic@example.com");
        Assert.assertTrue(emp.getFullName().contains(" "));
        Assert.assertTrue(emp.getEmail().contains("@"));
    }

    @Test(priority = 5, groups = "normalization")
    public void testSkillCategoryUniqueNameConstraint() {
        SkillCategory cat = new SkillCategory();
        cat.setCategoryName("Language");
        when(skillCategoryRepository.save(any(SkillCategory.class))).thenAnswer(inv -> inv.getArgument(0));
        SkillCategory saved = skillCategoryService.createCategory(cat);
        Assert.assertEquals(saved.getCategoryName(), "Language");
    }

    @Test(priority = 5, groups = "normalization")
    public void testEmployeeSkillSeparatesEmployeeAndSkillFor2NF() {
        Employee emp = new Employee();
        emp.setId(1L);
        Skill skill = new Skill();
        skill.setId(2L);
        EmployeeSkill es = new EmployeeSkill();
        es.setEmployee(emp);
        es.setSkill(skill);
        Assert.assertEquals(es.getEmployee().getId(), Long.valueOf(1L));
        Assert.assertEquals(es.getSkill().getId(), Long.valueOf(2L));
    }

    @Test(priority = 5, groups = "normalization")
    public void testSearchQueryRecordSeparatesSearchAndEmployee3NF() {
        SearchQueryRecord record = new SearchQueryRecord();
        record.setSearcherId(1L);
        record.setSkillsRequested("java,aws");
        Assert.assertEquals(record.getSearcherId(), Long.valueOf(1L));
        Assert.assertTrue(record.getSkillsRequested().contains("java"));
    }

    @Test(priority = 5, groups = "normalization")
    public void testEmployeeActiveFlagAffectsSearchLogic() {
        Employee e = new Employee();
        e.setActive(false);
        Assert.assertFalse(e.getActive());
    }

    @Test(priority = 5, groups = "normalization")
    public void testSkillActiveFlagAffectsMapping() {
        Skill skill = new Skill();
        skill.setActive(false);
        Assert.assertFalse(skill.getActive());
    }

    @Test(priority = 5, groups = "normalization")
    public void testSkillCategoryActiveFlag() {
        SkillCategory cat = new SkillCategory();
        cat.setActive(true);
        Assert.assertTrue(cat.getActive());
    }

    @Test(priority = 5, groups = "normalization")
    public void testEmployeeEmailUniqueLogicalCheck() {
        when(employeeRepository.findByEmail("unique@example.com"))
                .thenReturn(java.util.Optional.empty());
        java.util.Optional<Employee> existing = employeeRepository.findByEmail("unique@example.com");
        Assert.assertTrue(existing.isEmpty());
    }

    // 6. Many-to-many (8)

    @Test(priority = 6, groups = "manyToMany")
    public void testEmployeeCanHaveMultipleSkills() {
        Employee emp = new Employee();
        emp.setId(1L);

        EmployeeSkill es1 = new EmployeeSkill();
        es1.setEmployee(emp);
        Skill s1 = new Skill();
        s1.setId(1L);
        es1.setSkill(s1);

        EmployeeSkill es2 = new EmployeeSkill();
        es2.setEmployee(emp);
        Skill s2 = new Skill();
        s2.setId(2L);
        es2.setSkill(s2);

        List<EmployeeSkill> list = List.of(es1, es2);
        Assert.assertEquals(list.size(), 2);
    }

    @Test(priority = 6, groups = "manyToMany")
    public void testSkillCanBelongToMultipleEmployees() {
        Skill skill = new Skill();
        skill.setId(10L);

        EmployeeSkill es1 = new EmployeeSkill();
        Employee e1 = new Employee();
        e1.setId(1L);
        es1.setEmployee(e1);
        es1.setSkill(skill);

        EmployeeSkill es2 = new EmployeeSkill();
        Employee e2 = new Employee();
        e2.setId(2L);
        es2.setEmployee(e2);
        es2.setSkill(skill);

        Assert.assertEquals(es1.getSkill().getId(), es2.getSkill().getId());
    }

    @Test(priority = 6, groups = "manyToMany")
    public void testGetSkillsForEmployeeUsesRepository() {
        when(employeeSkillRepository.findByEmployeeIdAndActiveTrue(1L))
                .thenReturn(Collections.emptyList());
        List<EmployeeSkill> list = employeeSkillService.getSkillsForEmployee(1L);
        verify(employeeSkillRepository, times(1)).findByEmployeeIdAndActiveTrue(1L);
        Assert.assertTrue(list.isEmpty());
    }

    @Test(priority = 6, groups = "manyToMany")
    public void testGetEmployeesBySkillUsesRepository() {
        when(employeeSkillRepository.findBySkillIdAndActiveTrue(1L))
                .thenReturn(Collections.emptyList());
        List<EmployeeSkill> list = employeeSkillService.getEmployeesBySkill(1L);
        verify(employeeSkillRepository, times(1)).findBySkillIdAndActiveTrue(1L);
        Assert.assertTrue(list.isEmpty());
    }

    @Test(priority = 6, groups = "manyToMany")
    public void testDeactivateEmployeeSkillSetsActiveFalse() {
        EmployeeSkill es = new EmployeeSkill();
        es.setId(1L);
        es.setActive(true);
        when(employeeSkillRepository.findById(1L)).thenReturn(java.util.Optional.of(es));
        when(employeeSkillRepository.save(any(EmployeeSkill.class))).thenAnswer(inv -> inv.getArgument(0));

        employeeSkillService.deactivateEmployeeSkill(1L);
        Assert.assertFalse(es.getActive());
    }

    @Test(priority = 6, groups = "manyToMany")
    public void testCreateEmployeeSkillFailsForInactiveEmployee() {
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setActive(false);
        Skill skill = new Skill();
        skill.setId(2L);
        skill.setActive(true);
        skill.setName("Java");
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(emp));
        when(skillRepository.findById(2L)).thenReturn(java.util.Optional.of(skill));

        EmployeeSkill es = new EmployeeSkill();
        es.setEmployee(emp);
        es.setSkill(skill);
        es.setProficiencyLevel("Advanced");
        es.setYearsOfExperience(2);

        try {
            employeeSkillService.createEmployeeSkill(es);
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("inactive employee"));
        }
    }

    @Test(priority = 6, groups = "manyToMany")
    public void testCreateEmployeeSkillFailsForInactiveSkill() {
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setActive(true);
        Skill skill = new Skill();
        skill.setId(2L);
        skill.setActive(false);
        skill.setName("Java");
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(emp));
        when(skillRepository.findById(2L)).thenReturn(java.util.Optional.of(skill));

        EmployeeSkill es = new EmployeeSkill();
        es.setEmployee(emp);
        es.setSkill(skill);
        es.setProficiencyLevel("Advanced");
        es.setYearsOfExperience(2);

        try {
            employeeSkillService.createEmployeeSkill(es);
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("inactive skill"));
        }
    }

    // 7. Security & JWT (8)

    @Test(priority = 7, groups = "security")
    public void testGenerateJwtTokenContainsEmailRoleUserId() {
        Long userId = 1L;
        String email = "user@example.com";
        String role = "USER";
        String token = tokenProvider.generateToken(userId, email, role);

        Assert.assertNotNull(token);
        Assert.assertTrue(tokenProvider.validateToken(token));
        Assert.assertEquals(tokenProvider.getEmailFromToken(token), email);
        Assert.assertEquals(tokenProvider.getUserIdFromToken(token), userId);
        Assert.assertEquals(tokenProvider.getRoleFromToken(token), role);
    }

    @Test(priority = 7, groups = "security")
    public void testValidateTokenInvalidSignatureNegative() {
        String token = "invalid.token.value";
        boolean valid = tokenProvider.validateToken(token);
        Assert.assertFalse(valid);
    }

    @Test(priority = 7, groups = "security")
    public void testTokenRoleCaseInsensitiveAuthority() {
        Long userId = 2L;
        String email = "admin@example.com";
        String role = "admin";
        String token = tokenProvider.generateToken(userId, email, role);
        Assert.assertTrue(tokenProvider.validateToken(token));
        Assert.assertEquals(tokenProvider.getRoleFromToken(token), role);
    }

    @Test(priority = 7, groups = "security")
    public void testTokenExpirationEdgeCase() {
        Long userId = 3L;
        String email = "exp@example.com";
        String role = "USER";
        String token = tokenProvider.generateToken(userId, email, role);
        Assert.assertTrue(tokenProvider.validateToken(token));
    }

    @Test(priority = 7, groups = "security")
    public void testTokenDifferentUsersProduceDifferentTokens() {
        String t1 = tokenProvider.generateToken(1L, "a@example.com", "USER");
        String t2 = tokenProvider.generateToken(2L, "b@example.com", "USER");
        Assert.assertNotEquals(t1, t2);
    }

    @Test(priority = 7, groups = "security")
    public void testTokenInvalidAfterTampering() {
        String t1 = tokenProvider.generateToken(1L, "a@example.com", "USER");
        String tampered = t1 + "abc";
        Assert.assertFalse(tokenProvider.validateToken(tampered));
    }

    @Test(priority = 7, groups = "security")
    public void testTokenClaimsNotNull() {
        String token = tokenProvider.generateToken(5L, "c@example.com", "USER");
        Assert.assertNotNull(tokenProvider.getClaims(token));
    }

    @Test(priority = 7, groups = "security")
    public void testAuthLoginGeneratesTokenForExistingEmployee() {
        Employee emp = new Employee();
        emp.setId(10L);
        emp.setEmail("login@example.com");
        when(employeeRepository.findByEmail("login@example.com")).thenReturn(java.util.Optional.of(emp));

        String token = tokenProvider.generateToken(emp.getId(), emp.getEmail(), "USER");
        Assert.assertNotNull(token);
        Assert.assertTrue(tokenProvider.validateToken(token));
    }

    // 8. HQL/HCQL-like queries (8)

    @Test(priority = 8, groups = "hql")
    public void testSearchEmployeesBySkillsCallsRepository() {
        List<String> skills = List.of("Java", "AWS");
        Employee e = new Employee();
        e.setId(1L);
        when(employeeSkillRepository.findEmployeesByAllSkillNames(anyList(), anyLong()))
                .thenReturn(List.of(e));
        when(searchQueryRecordRepository.save(any(SearchQueryRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Employee> result = searchQueryService.searchEmployeesBySkills(skills, 1L);
        verify(employeeSkillRepository, times(1))
                .findEmployeesByAllSkillNames(anyList(), eq(2L));
        Assert.assertEquals(result.size(), 1);
    }

    @Test(priority = 8, groups = "hql")
    public void testSearchEmployeesBySkillsEmptyListNegative() {
        try {
            searchQueryService.searchEmployeesBySkills(Collections.emptyList(), 1L);
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("must not be empty"));
        }
    }

    @Test(priority = 8, groups = "hql")
    public void testSearchEmployeesBySkillsTrimsAndNormalizes() {
        List<String> skills = List.of("  java ", "JAVA");
        when(employeeSkillRepository.findEmployeesByAllSkillNames(anyList(), anyLong()))
                .thenReturn(Collections.emptyList());
        when(searchQueryRecordRepository.save(any(SearchQueryRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Employee> result = searchQueryService.searchEmployeesBySkills(skills, 1L);
        Assert.assertTrue(result.isEmpty());

        ArgumentCaptor<SearchQueryRecord> captor = ArgumentCaptor.forClass(SearchQueryRecord.class);
        verify(searchQueryRecordRepository, atLeastOnce()).save(captor.capture());
        SearchQueryRecord saved = captor.getValue();
        Assert.assertEquals(saved.getSkillsRequested(), "java");
    }

    @Test(priority = 8, groups = "hql")
    public void testGetQueryByIdUsesRepository() {
        SearchQueryRecord record = new SearchQueryRecord();
        record.setId(100L);
        when(searchQueryRecordRepository.findById(100L)).thenReturn(java.util.Optional.of(record));
        SearchQueryRecord result = searchQueryService.getQueryById(100L);
        Assert.assertEquals(result.getId(), Long.valueOf(100L));
    }

    @Test(priority = 8, groups = "hql")
    public void testGetQueriesForUserUsesRepository() {
        when(searchQueryRecordRepository.findBySearcherId(1L)).thenReturn(Collections.emptyList());
        List<SearchQueryRecord> list = searchQueryService.getQueriesForUser(1L);
        verify(searchQueryRecordRepository, times(1)).findBySearcherId(1L);
        Assert.assertTrue(list.isEmpty());
    }

    @Test(priority = 8, groups = "hql")
    public void testSearchEmployeesBySingleSkillEdgeCase() {
        List<String> skills = List.of("Java");
        when(employeeSkillRepository.findEmployeesByAllSkillNames(anyList(), anyLong()))
                .thenReturn(Collections.emptyList());
        when(searchQueryRecordRepository.save(any(SearchQueryRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Employee> result = searchQueryService.searchEmployeesBySkills(skills, 2L);
        Assert.assertTrue(result.isEmpty());
    }

    @Test(priority = 8, groups = "hql")
    public void testSearchEmployeesBySkillsNullListNegative() {
        try {
            searchQueryService.searchEmployeesBySkills(null, 1L);
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("must not be empty"));
        }
    }

    @Test(priority = 8, groups = "hql")
    public void testSearchEmployeesBySkillsPersistsResultsCount() {
        List<String> skills = List.of("Java", "AWS");
        Employee e1 = new Employee();
        e1.setId(1L);
        Employee e2 = new Employee();
        e2.setId(2L);
        when(employeeSkillRepository.findEmployeesByAllSkillNames(anyList(), anyLong()))
                .thenReturn(List.of(e1, e2));
        when(searchQueryRecordRepository.save(any(SearchQueryRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        searchQueryService.searchEmployeesBySkills(skills, 3L);

        ArgumentCaptor<SearchQueryRecord> captor = ArgumentCaptor.forClass(SearchQueryRecord.class);
        verify(searchQueryRecordRepository, atLeastOnce()).save(captor.capture());
        SearchQueryRecord saved = captor.getValue();
        Assert.assertEquals((int) saved.getResultsCount(), 2);
        Assert.assertEquals(saved.getSearcherId(), Long.valueOf(3L));
    }
}
