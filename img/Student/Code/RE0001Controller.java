package jp.drjoy.service.web.api.controller.registration;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jp.drjoy.backend.registration.domain.model.Student;
import jp.drjoy.service.framework.utils.Strings;
import jp.drjoy.service.web.api.service.RegistrationService;
import jp.drjoy.service.web.model.ReSideMenuSettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * RE0001 サイドメニューの並び順変更
 */
@RestController
public class RE0001Controller {

    private RegistrationService registrationService;

    public RE0001Controller(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @ApiOperation(value = "RE0001 side menu get")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "get side menu successfully"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @RequestMapping(value = "${endpoint.re.dr.side_menu}", method = RequestMethod.GET)
    public ResponseEntity<ReSideMenuSettings> getSideMenuSettings() {
        ReSideMenuSettings settings = registrationService.getSideMenu();
        return new ResponseEntity<>(settings, HttpStatus.OK);
    }

    @ApiOperation(value = "RE0001 side menu save")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "put side menu successfully"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @RequestMapping(value = "${endpoint.re.dr.side_menu}", method = RequestMethod.PUT)
    public ResponseEntity<Void> putSideMenuSettings(
        @Valid @RequestBody ReSideMenuSettings settings) {
        registrationService.putSideMenu(settings);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // PhucLQ
    @ApiOperation(value = "Student get info Student")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "save Student successfully")
    })
    @RequestMapping(value = "${endpoint.at.dr.staff.department.add_student}", method = RequestMethod.GET)
    public ResponseEntity<List<Student>> addStudent(
        @ApiParam(value = "name", required = true)
        @RequestParam(value = "name") String name,
        @ApiParam(value = "age", required = true)
        @RequestParam(value = "age") int age,
        @ApiParam(value = "address", required = true)
        @RequestParam(value = "address") String address,
        @ApiParam(value = "gpa", required = true)
        @RequestParam(value = "gpa") float gpa) {
        if (Strings.isEmpty(name) || age < 1 || Strings.isEmpty(address) || gpa < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Student> studentList = registrationService.addStudent(Strings.nvl(name), age, address, gpa);
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }

    @ApiOperation(value = "Student get info Student")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "eidt Student successfully")
    })
    @RequestMapping(value = "${endpoint.at.dr.staff.department.edit_student}", method = RequestMethod.GET)
    public ResponseEntity<List<Student>> editStudent(
        @ApiParam(value = "id", required = true)
        @RequestParam(value = "id") String id,
        @ApiParam(value = "name", required = true)
        @RequestParam(value = "name") String name,
        @ApiParam(value = "age", required = true)
        @RequestParam(value = "age") int age,
        @ApiParam(value = "address", required = true)
        @RequestParam(value = "address") String address,
        @ApiParam(value = "gpa", required = true)
        @RequestParam(value = "gpa") float gpa) {
        if (Strings.isEmpty(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Student> studentList = registrationService.editStudent(Strings.nvl(id), Strings.nvl(name), age, address, gpa);
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }

    @RequestMapping(value = "${endpoint.at.dr.staff.department.del_student}", method = RequestMethod.GET)
    public ResponseEntity<Void> delStudent(
        @ApiParam(value = "id", required = true)
        @RequestParam(value = "id") String id) {
        if (Strings.isEmpty(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        registrationService.delStudent(Strings.nvl(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "${endpoint.at.dr.staff.department.sort_name_student}", method = RequestMethod.GET)
    public ResponseEntity<List<Student>> sortByNameStudent(
        @ApiParam(value = "id", required = true)
        @RequestParam(value = "id") String id) {
        if (Strings.isEmpty(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Student> studentList = registrationService.getSortByNameStudent();
        return new ResponseEntity<>(/*studentList,*/HttpStatus.OK);
    }

    @RequestMapping(value = "${endpoint.at.dr.staff.department.sort_pga_student}", method = RequestMethod.GET)
    public ResponseEntity<List<Student>> sortByNameStudent() {
        // List<Student> studentList = registrationService.getSortByGpaStudent(Strings.nvl(id));
        return new ResponseEntity<>(/*studentList,*/HttpStatus.OK);
    }

    @RequestMapping(value = "${endpoint.at.dr.staff.department.show_student}", method = RequestMethod.GET)
    public ResponseEntity<List<Student>> showStudent() {
        List<Student> studentList = registrationService.showStudent();
        return new ResponseEntity<>(/*studentList,*/HttpStatus.OK);
    }
}
