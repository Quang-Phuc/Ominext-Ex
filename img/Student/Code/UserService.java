package jp.drjoy.backend.registration.service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.protobuf.Empty;
import com.google.protobuf.ProtocolStringList;
import jp.drjoy.backend.registration.domain.BranchRepository;
import jp.drjoy.backend.registration.domain.ContactRepository;
import jp.drjoy.backend.registration.domain.DepartmentRepository;
import jp.drjoy.backend.registration.domain.HandlingHospitalRepository;
import jp.drjoy.backend.registration.domain.IdentificationRepository;
import jp.drjoy.backend.registration.domain.MailChangeRepository;
import jp.drjoy.backend.registration.domain.OfficeRepository;
import jp.drjoy.backend.registration.domain.OfficeSettingsRepository;
import jp.drjoy.backend.registration.domain.OfficeUserRepository;
import jp.drjoy.backend.registration.domain.OfficeUserSettingsRepository;
import jp.drjoy.backend.registration.domain.ProfileRepository;
import jp.drjoy.backend.registration.domain.StaffPinRepository;
import jp.drjoy.backend.registration.domain.StudentRepository;
import jp.drjoy.backend.registration.domain.UserConnectionRepository;
import jp.drjoy.backend.registration.domain.UserProvisionalEntryRepository;
import jp.drjoy.backend.registration.domain.UserRepository;
import jp.drjoy.backend.registration.domain.model.Branch;
import jp.drjoy.backend.registration.domain.model.Contact;
import jp.drjoy.backend.registration.domain.model.Department;
import jp.drjoy.backend.registration.domain.model.GenderType;
import jp.drjoy.backend.registration.domain.model.HandlingHospital;
import jp.drjoy.backend.registration.domain.model.Identification;
import jp.drjoy.backend.registration.domain.model.IdentifyStatus;
import jp.drjoy.backend.registration.domain.model.MRUser;
import jp.drjoy.backend.registration.domain.model.MailChangeRequest;
import jp.drjoy.backend.registration.domain.model.ManagementLevel;
import jp.drjoy.backend.registration.domain.model.MedicalOffice;
import jp.drjoy.backend.registration.domain.model.MeetingRestriction;
import jp.drjoy.backend.registration.domain.model.MenuItem;
import jp.drjoy.backend.registration.domain.model.Office;
import jp.drjoy.backend.registration.domain.model.OfficeSettings;
import jp.drjoy.backend.registration.domain.model.OfficeType;
import jp.drjoy.backend.registration.domain.model.OfficeUser;
import jp.drjoy.backend.registration.domain.model.OfficeUserSettings;
import jp.drjoy.backend.registration.domain.model.Profile;
import jp.drjoy.backend.registration.domain.model.PublishingType;
import jp.drjoy.backend.registration.domain.model.Settings;
import jp.drjoy.backend.registration.domain.model.SideMenuSettings;
import jp.drjoy.backend.registration.domain.model.SpecializedDepartment;
import jp.drjoy.backend.registration.domain.model.StaffPin;
import jp.drjoy.backend.registration.domain.model.Student;
import jp.drjoy.backend.registration.domain.model.User;
import jp.drjoy.backend.registration.domain.model.UserConnection;
import jp.drjoy.backend.registration.domain.model.UserProvisionalEntry;
import jp.drjoy.backend.registration.dto.MrShareInfoStatusDeletionDTO;
import jp.drjoy.backend.registration.dto.MrShareInfoStatusDeletionReason;
import jp.drjoy.backend.registration.dto.UserGrantServiceAttendanceCondition;
import jp.drjoy.backend.registration.dto.UserProvisionalCondition;
import jp.drjoy.backend.registration.dto.UserSearchCondition;
import jp.drjoy.backend.registration.utils.AccountStatusUtils;
import jp.drjoy.backend.registration.utils.ValidateUtils;
import jp.drjoy.core.autogen.grpc.attendance.ATRequestResult;
import jp.drjoy.core.autogen.grpc.attendance.ATStaffRequestItem;
import jp.drjoy.core.autogen.grpc.attendance.ATUpdatedDepartment;
import jp.drjoy.core.autogen.grpc.attendance.OfficeUserIdsInDept;
import jp.drjoy.core.autogen.grpc.common.CMNPage;
import jp.drjoy.core.autogen.grpc.group.GRCheckMemberIsOnlyAdminResponse;
import jp.drjoy.core.autogen.grpc.group.GRListGroupBelongUserResponse;
import jp.drjoy.core.autogen.grpc.master.MAListMrJobTypesResponse;
import jp.drjoy.core.autogen.grpc.master.MAMrJobType;
import jp.drjoy.core.autogen.grpc.master.MAPrefecture;
import jp.drjoy.core.autogen.grpc.meeting.MEActionChangeStatus;
import jp.drjoy.core.autogen.grpc.meeting.MEGetMediatedDoctorResponse;
import jp.drjoy.core.autogen.grpc.meeting.MEListHandleUsersResponse;
import jp.drjoy.core.autogen.grpc.meeting.MEUser;
import jp.drjoy.core.autogen.grpc.meeting.MediatedDoctorItem;
import jp.drjoy.core.autogen.grpc.registration.DelStudentRequest;
import jp.drjoy.core.autogen.grpc.registration.EditStudentRequest;
import jp.drjoy.core.autogen.grpc.registration.GetOfficeUser;
import jp.drjoy.core.autogen.grpc.registration.GetUserIdFromListRequest;
import jp.drjoy.core.autogen.grpc.registration.GetUserIdFromListResponse;
import jp.drjoy.core.autogen.grpc.registration.ListStudentResponse;
import jp.drjoy.core.autogen.grpc.registration.REAddListUserConnectionRequest;
import jp.drjoy.core.autogen.grpc.registration.REAddUserConnection;
import jp.drjoy.core.autogen.grpc.registration.REBranch;
import jp.drjoy.core.autogen.grpc.registration.RECountUserByListOfficeUserIdResponse;
import jp.drjoy.core.autogen.grpc.registration.RECreateDrAdminUserRequest;
import jp.drjoy.core.autogen.grpc.registration.RECreateDrAdminUserResponse;
import jp.drjoy.core.autogen.grpc.registration.RECreateUserRequest;
import jp.drjoy.core.autogen.grpc.registration.RECreateUserResponse;
import jp.drjoy.core.autogen.grpc.registration.REDeleteListUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REDeleteUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REDepartment;
import jp.drjoy.core.autogen.grpc.registration.REDownloadProvisionalUsersResponse;
import jp.drjoy.core.autogen.grpc.registration.REFuncAuthority;
import jp.drjoy.core.autogen.grpc.registration.REFuncAuthoritySet;
import jp.drjoy.core.autogen.grpc.registration.REGender;
import jp.drjoy.core.autogen.grpc.registration.REGetDepartmentResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetDrugStoreByEmailRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetDrugStoreByEmailResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListDepartmentByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListDepartmentByOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListDepartmentIdByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListDepartmentIdByOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListManagerRequestRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListManagerRequestResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListOfficeUserByConditionRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListOfficeUserByConditionResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserByConditionRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserByConditionResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserByRequestManagementAuthorityConditionRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserNameByListUserIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserNameByListUserIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetOfficeUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetOfficeUsersByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetOfficeUsersByOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetPrepareEditInsideGroupResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetSSOTokenUserInfoResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetStaffInsideResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetUseEntryResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetUserByListRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserByListResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetUserByMailAddressRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserByMailAddressResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetUserByOfficeUserIdOrUserIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserByOfficeUserIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserByOfficeUserIdsOrUserIdsRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserEntryRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserGrantServiceAttendanceRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserGrantServiceAttendanceResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetUserIdListByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserIdListByOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetUserInfoByEmailRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserInfoByEmailResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetUserListRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserProvisionalFromListResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REHandlingHospital;
import jp.drjoy.core.autogen.grpc.registration.REListDepartmentId;
import jp.drjoy.core.autogen.grpc.registration.REListMailUnConfirmAdditionalRequest;
import jp.drjoy.core.autogen.grpc.registration.REListMailUnConfirmAdditionalResponse;
import jp.drjoy.core.autogen.grpc.registration.REListOfficeUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REListOfficeUserResponse;
import jp.drjoy.core.autogen.grpc.registration.REListProvisionalUsersRequest;
import jp.drjoy.core.autogen.grpc.registration.REListProvisionalUsersResponse;
import jp.drjoy.core.autogen.grpc.registration.REListUser;
import jp.drjoy.core.autogen.grpc.registration.REListUserByOfficeUserIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REListUserByOfficeUserIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REListUserByOfficesResponse;
import jp.drjoy.core.autogen.grpc.registration.REListUsersRequest;
import jp.drjoy.core.autogen.grpc.registration.REListUsersResponse;
import jp.drjoy.core.autogen.grpc.registration.REMailUnConfirmAdditionalItem;
import jp.drjoy.core.autogen.grpc.registration.REManagementAuthority;
import jp.drjoy.core.autogen.grpc.registration.REManagementLevel;
import jp.drjoy.core.autogen.grpc.registration.REManagerRequestItem;
import jp.drjoy.core.autogen.grpc.registration.REOfficeType;
import jp.drjoy.core.autogen.grpc.registration.REOfficeUser;
import jp.drjoy.core.autogen.grpc.registration.REPrepareDepartmentUser;
import jp.drjoy.core.autogen.grpc.registration.REPublishingType;
import jp.drjoy.core.autogen.grpc.registration.REPutUserOfficeRequest;
import jp.drjoy.core.autogen.grpc.registration.REPutUserPasswordRequest;
import jp.drjoy.core.autogen.grpc.registration.REPutUserStatusRequest;
import jp.drjoy.core.autogen.grpc.registration.RESSOTokenUserInfoAttribute;
import jp.drjoy.core.autogen.grpc.registration.RESpecializedDepartment;
import jp.drjoy.core.autogen.grpc.registration.REStaff;
import jp.drjoy.core.autogen.grpc.registration.REStaffItem;
import jp.drjoy.core.autogen.grpc.registration.REUpdateImageProfileOnlyRequest;
import jp.drjoy.core.autogen.grpc.registration.REUpdateImageProfileRequest;
import jp.drjoy.core.autogen.grpc.registration.REUser;
import jp.drjoy.core.autogen.grpc.registration.REUserAndUserId;
import jp.drjoy.core.autogen.grpc.registration.REUserByListInfo;
import jp.drjoy.core.autogen.grpc.registration.REUserInfoFromEmail;
import jp.drjoy.core.autogen.grpc.registration.REUserItem;
import jp.drjoy.core.autogen.grpc.registration.REUserListRespone;
import jp.drjoy.core.autogen.grpc.registration.REUserResponse;
import jp.drjoy.core.autogen.grpc.registration.SaveStudentRequest;
import jp.drjoy.core.autogen.grpc.registration.StudentResponse;
import jp.drjoy.core.autogen.grpc.registration.UpdateImageProfileAndIdentificationImageRequest;
import jp.drjoy.core.autogen.grpc.rtm.RTCheckIsExclusivelyAdminResponse;
import jp.drjoy.core.autogen.grpc.shift.SHUpdateShiftDataWhenChangeDept;
import jp.drjoy.core.autogen.grpc.sidemenu.FBAtRequestNumber;
import jp.drjoy.core.autogen.grpc.sidemenu.REProvisionalUsersFireBaseRequest;
import jp.drjoy.core.autogen.grpc.sidemenu.REProvisionalUsersFireBaseResponse;
import jp.drjoy.core.autogen.grpc.sidemenu.SMCreateRequest;
import jp.drjoy.core.autogen.grpc.sidemenu.SMUpdateOrderRequest;
import jp.drjoy.core.autogen.grpc.sidemenu.SMUpdateRequest;
import jp.drjoy.core.autogen.grpc.sidemenu.SMUpdateUserRequest;
import jp.drjoy.core.autogen.grpc.sidemenu.SMUser;
import jp.drjoy.core.autogen.grpc.sidemenu.UpdateUserInfoRequest;
import jp.drjoy.core.autogen.grpc.sidemenu.UserRequestNumber;
import jp.drjoy.service.framework.constant.Message;
import jp.drjoy.service.framework.grpc.ServiceStatus;
import jp.drjoy.service.framework.model.AccountStatus;
import jp.drjoy.service.framework.model.AccountStatuses;
import jp.drjoy.service.framework.model.FuncAuthority;
import jp.drjoy.service.framework.model.FuncAuthoritySet;
import jp.drjoy.service.framework.model.ManagementAuthority;
import jp.drjoy.service.framework.publisher.NotificationID;
import jp.drjoy.service.framework.publisher.ServiceType;
import jp.drjoy.service.framework.security.EncryptProxy;
import jp.drjoy.service.framework.security.model.LoginInfo;
import jp.drjoy.service.framework.utils.Dates;
import jp.drjoy.service.framework.utils.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static jp.drjoy.backend.registration.service.DepartmentService.PATH_DEPARTMENT;

/**
 * ユーザー情報に関わるサービス.
 * <p>
 * Created by k.sumi 7/27/2017.
 */
@Service
public class UserService {
    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public static final String REQUEST_PARAM_DEFAULT_VALUE = "59f6a859c911ac0c24df5953";
    private static final String SHARE_STATUS_DENY = "share status deny";
    private static final String FUNC_AUTHORITY_FP1 = ".*FPS_*.*1.*";

    private final static int DATE_EXPIRE = 2;

    /** オフィスユーザ情報初期値(jobType)【その他】 */
    private static final String DEFAULT_OFFICE_USER_JOB_TYPE = "J9999";
    /** オフィスユーザ情報初期値(lastName) */
    private static final String DEFAULT_OFFICE_USER_LAST_NAME = "Dr.JOY";
    /** オフィスユーザ情報初期値(lastNameKana) */
    private static final String DEFAULT_OFFICE_USER_LAST_NAME_KANA = "どくたーじょい";
    /** オフィスユーザ情報初期値(firstName) */
    private static final String DEFAULT_OFFICE_USER_FIRST_NAME = "管理者";
    /** オフィスユーザ情報初期値(firstNameKana) */
    private static final String DEFAULT_OFFICE_USER_FIRST_NAME_KANA = "かんりしゃ";

    /** 仮パスワード最小桁数 */
    private static final int PASSWORD_MIN_LENGTH = 6;

    private static final int LOGIN_ID_MIN_LENGTH = 8;

    // Instance variables
    // ------------------------------------------------------------------------
    public final static String NOT_FOUND_USER = "Not Found Specified User";
    private final static String NOT_FOUND_OFFICE = "Not Found Office User";
    private final static String NOT_FOUND_DEPARTMENT = "Not Found Department";

    private final static String ADMIN_GROUP =
            "cancel delete or lock account because user is admin other group";

    /** Message -> invalid format date */
    private static final String DATE_INVALID_FORMAT = "Invalid date format";

    /** Message for cant save when only a full privilege */
    private final static String INVALID_FULL_PRIVILEGE =
            "Can not save OfficeUser by only full privilege";

    /** Message for existed loginId */
    private final static String LOGINID_EXISTED = "Login id was existed";
    private final static String ENTRYTOKEN_NULL = "EntryToken null";
    private final static String ENTRYTOKEN_INVALD = "EntryToken invalid";
    private final static String INVALID_IDENTIFICATION = "Invalid Identification";
    private static final String NOT_PERMISSION = "Not Permission";
    private static final String DOCTOR = "J0001";
    private static final String MAIL_ADDRESS_INVALD = "Email address not validate";
    private static final String MAIL_ADDITION_INVALD = "Email addition not validate";

    private static final String NOT_SPECIALIZED_DEPARTMENT =
            "Param not found SpecializedDepartments";
    private static final String TIME_ZONE = "T12:00:00+0000";
    private final static String INVALID_TOCKEN = "Invalid tocken";


    private static final String FUNCTIONID_1 = "CA0002";
    private static final String FUNCTIONID_2 = "ME0001";
    private static final String FUNCTIONID_4 = "CH0005";
    private static final String FUNCTIONID_5 = "CH0007";
    private static final String FUNCTIONID_6 = "GR0015";
    private static final String FUNCTIONID_7 = "GR0016";

    private static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";

    public static final String RE0016_E001 = "RE0016_E001";
    // このユーザーはあるグループの唯一の管理者の為、削除できません。
    public static final String RE0016_E002 = "RE0016_E002";

    public static final String RE0020_E001 = "RE0020_E001";
    // このユーザーはあるメッセージルームの唯一の管理者の為、削除できません。
    public static final String RE0020_E002 = "RE0020_E002";
    private static final String ALL_STAFF = "ALL";
    private static final String DEPARTMENT_ROOT = "/";

    private final DepartmentService departmentService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final UserProvisionalEntryRepository userProvisionalEntryRepository;
    private final EncryptProxy encryptProxy;
    private final BranchRepository branchRepository;
    private final IdentificationRepository identificationRepository;
    private final ProfileRepository profileRepository;
    private final OfficeUserRepository officeUserRepository;
    private final MailChangeRepository mailChangeRepository;
    private final OfficeRepository officeRepository;
    private final UserConnectionRepository userConnectionRepository;
    private final HandlingHospitalRepository handlingHospitalRepository;
    private final ContactRepository contactRepository;
    private final StaffPinRepository staffPinRepository;
    private final OfficeUserSettingsRepository officeUserSettingsRepository;
    private  StudentRepository studentRepository;

    private GrpcClientCalendarService calendarService;
    private GrpcClientMasterService grpcClientMasterService;
    private GrpcClientMeetingService grpcClientMeetingService;
    private final CommonService commonService;
    private GrpcClientGroupService grpcClientGroupService;
    private GrpcClientSideMenuService grpcClientSideMenuService;
    private OfficeSettingsRepository officeSettingsRepository;
    private GrpcClientElasticService grpcClientElasticService;
    private GrpcClientRtmService grpcClientRtmService ;
    private GrpcClientCmsService grpcClientCmsService ;
    private GrpcClientShiftService grpcClientShiftService;
    private GrpcClientAttendanceService grpcClientAttendanceService;
    private GrpcClientWebMeetingService grpcClientWebMeetingService;
    private final MrShareInfoStatusService mrShareInfoStatusService;
    private GrpcClientAttendanceSyncService grpcClientAttendanceSyncService;
    private GrpcClientPresentationService grpcClientPresentationService;


    // Constructors
    // ------------------------------------------------------------------------
    @Autowired
    public UserService(@Lazy DepartmentService departmentService,
                OfficeRepository officeRepository,
                UserRepository userRepository,
                DepartmentRepository departmentRepository,
                UserProvisionalEntryRepository userProvisionalEntryRepository,
                EncryptProxy encryptProxy,
                BranchRepository branchRepository,
                IdentificationRepository identificationRepository, ProfileRepository profileRepository,
                OfficeUserRepository officeUserRepository,
                MailChangeRepository mailChangeRepository,
                UserConnectionRepository userConnectionRepository,
                HandlingHospitalRepository handlingHospitalRepository,
                ContactRepository contactRepository,
                GrpcClientCalendarService calendarService,
                GrpcClientMasterService grpcClientMasterService,
                GrpcClientMeetingService grpcClientMeetingService,
                GrpcClientGroupService grpcClientGroupService,
                CommonService commonService,
                GrpcClientSideMenuService grpcClientSideMenuService,
                OfficeSettingsRepository officeSettingsRepository,
                GrpcClientElasticService grpcClientElasticService,
                GrpcClientRtmService grpcClientRtmService,
                GrpcClientCmsService grpcClientCmsService,
                GrpcClientShiftService grpcClientShiftService,
                GrpcClientAttendanceService grpcClientAttendanceService,
                GrpcClientWebMeetingService grpcClientWebMeetingService,
                MrShareInfoStatusService mrShareInfoStatusService,
                       GrpcClientAttendanceSyncService grpcClientAttendanceSyncService,
                       GrpcClientPresentationService grpcClientPresentationService,
                       StaffPinRepository staffPinRepository,
                       OfficeUserSettingsRepository officeUserSettingsRepository,StudentRepository studentRepository) {
        this.departmentService = departmentService;
        this.officeRepository = officeRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.userProvisionalEntryRepository = userProvisionalEntryRepository;
        this.encryptProxy = encryptProxy;
        this.branchRepository = branchRepository;
        this.identificationRepository = identificationRepository;
        this.profileRepository = profileRepository;
        this.officeUserRepository = officeUserRepository;
        this.mailChangeRepository = mailChangeRepository;
        this.userConnectionRepository = userConnectionRepository;
        this.handlingHospitalRepository = handlingHospitalRepository;
        this.contactRepository = contactRepository;
        this.calendarService = calendarService;
        this.grpcClientMasterService = grpcClientMasterService;
        this.grpcClientMeetingService = grpcClientMeetingService;
        this.grpcClientGroupService = grpcClientGroupService;
        this.commonService = commonService;
        this.grpcClientSideMenuService = grpcClientSideMenuService;
        this.officeSettingsRepository = officeSettingsRepository;
        this.grpcClientElasticService = grpcClientElasticService;
        this.grpcClientRtmService = grpcClientRtmService ;
        this.grpcClientCmsService = grpcClientCmsService ;
        this.grpcClientShiftService = grpcClientShiftService;
        this.grpcClientAttendanceService = grpcClientAttendanceService;
        this.grpcClientWebMeetingService = grpcClientWebMeetingService;
        this.mrShareInfoStatusService = mrShareInfoStatusService;
        this.grpcClientAttendanceSyncService = grpcClientAttendanceSyncService;
        this.staffPinRepository = staffPinRepository;
        this.officeUserSettingsRepository = officeUserSettingsRepository;
        this.grpcClientPresentationService = grpcClientPresentationService;
    }

    // Public methods
    // ------------------------------------------------------------------------

    /**
     * get UserEntry
     *
     * @param request Get Entry Request
     * @return Get Entry Response
     */
    public REGetUseEntryResponse getUserEntry(final REGetUserEntryRequest request) {
        UserProvisionalEntry userProvisionalEntry =
                userProvisionalEntryRepository.findUserProvisionalEntryByEntryToken(
                        request.getEntryToken());

        if (userProvisionalEntry == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(ENTRYTOKEN_NULL)
                    .addError(Message.COMMON_GET_FAILED).asStatusRuntimeException();
        }

        if (!userProvisionalEntry.isStatus()) {
            throw ServiceStatus.NOT_FOUND.withMessage(ENTRYTOKEN_INVALD)
                    .addError(Message.COMMON_GET_FAILED).asStatusRuntimeException();
        }


        Office office = null;
        if (StringUtils.isNotBlank(userProvisionalEntry.getMedicalOfficeId())) {
            office = officeRepository.findOne(userProvisionalEntry.getMedicalOfficeId());
            if (office == null) {
                throw ServiceStatus.NOT_FOUND.withMessage("Office is not existing")
                        .addError(Message.COMMON_GET_FAILED).asStatusRuntimeException();
            }
        }

        Office inviteesOffice = null;
        if (StringUtils.isNotBlank(userProvisionalEntry.getInviteesOfficeId())) {
            inviteesOffice = officeRepository.findOne(userProvisionalEntry.getInviteesOfficeId());
            if (inviteesOffice == null) {
                throw ServiceStatus.NOT_FOUND.withMessage("Office is not existing")
                        .addError(Message.COMMON_GET_FAILED).asStatusRuntimeException();
            }
        }

        return REGetUseEntryResponse.newBuilder()
                .setEntryToken(Strings.nvl(userProvisionalEntry.getEntryToken()))
                .setInviteesOfficeId(Strings.nvl(userProvisionalEntry.getInviteesOfficeId()))
                .setInviteesOfficeName(inviteesOffice != null ? Strings.nvl(inviteesOffice.getName()) : "")
                .setMedicalOfficeId(Strings.nvl(userProvisionalEntry.getMedicalOfficeId()))
                .setMedicalOfficeName(office != null ? office.getName() : "")
                .setIsRestrictedKeyCode(userProvisionalEntry.isRestrictedKeyCode())
                .build();
    }

    public RECreateUserResponse createUserEntry(final RECreateUserRequest request) {

        // Check Office is existing
        if (StringUtils.isBlank(request.getMedicalOfficeId())
                || officeRepository.findOne(request.getMedicalOfficeId()) == null) {
            throw ServiceStatus.NOT_FOUND.withMessage("MedicalOffice is not existing")
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        // CHECK EMAIL IS EXISTING:
        // 1. Check loginId
        if (userRepository.findFirstByLoginId(request.getMailAddress()) != null) {
            throw ServiceStatus.NOT_FOUND.withMessage("mailAddress is invalid")
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }
        // 2. Check mail, optional mail
        if (officeUserRepository.findOfficeUserByMailAddressOrAdditionalMailAddresses(
                request.getMailAddress(), request.getMailAddress()) != null) {
            throw ServiceStatus.NOT_FOUND.withMessage("mailAddress is invalid")
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        Identification inIdentification = new Identification();
        inIdentification.setFileName(request.getIdentificationFileName());
        //inIdentification.setImageUrl(request.getIdentificationImageUrl());
        try {
            if (request.getIdentificationUpdated().length() != 0) {
                inIdentification.setUpdated(Dates.parseISO(request.getIdentificationUpdated()));
            } else {
                inIdentification.setUpdated(Dates.now());
            }
        } catch (IllegalArgumentException e) {
            throw ServiceStatus.NOT_FOUND.withMessage(INVALID_IDENTIFICATION)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        // Save user
        User user = new User();
        user.setLoginId(request.getMailAddress());
        user.setPassword(encryptProxy.encrypt(request.getPassword()));
        user.setMailAddress(request.getMailAddress());
        user = userRepository.save(user);

        // Save Profile
        Profile profile = new Profile();
        profile.setPlaceBornIn(request.getPlaceBornIn());
        profile.setHobby(request.getHobby());
        //profile.setImage(request.getImageUrl());
        profile.setMessage(request.getMessage());
        REGender genderType = request.getGender();
        profile.setGenderType(GenderType.valueOf(genderType.name()));

        profileRepository.save(profile);

        MRUser mrUser = new MRUser();
        mrUser.setUserId(user.getId());
        mrUser.setLoginId(request.getMailAddress());
        mrUser.setOfficeId(request.getPharmacyOfficeId());
        mrUser.setOfficeType(OfficeType.PHARMACY);
        mrUser.setGraduatedPharmacy(request.getPharmacyGraduation());
        mrUser.setIsVisibleToAllHospital(false);

        try {
            if (request.getBirthDate().length() != 0) {
                mrUser.setBirthDate(Dates.parseISO(request.getBirthDate() + TIME_ZONE));
            }
        } catch (IllegalArgumentException e) {
            throw ServiceStatus.INVALID_ARGUMENT.withMessage(DATE_INVALID_FORMAT)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }
        mrUser.setFirstName(request.getFirstName());
        mrUser.setFirstNameKana(request.getFirtNameKana());
        mrUser.setLastName(request.getLastName());
        mrUser.setLastNameKana(request.getLastNameKana());
        mrUser.setExperience(Integer.valueOf(request.getExperiences()));

        // Specialized area
        List<String> requestSpecializedAreaList = request.getHandleFieldsList();
        mrUser.setHandleFields(requestSpecializedAreaList);

        // Save Branch
        Branch branch = new Branch();
        branch.setName(request.getOfficeName());
        branch.setAddress(request.getBranchAddress());
        branch.setDepartment(request.getBranchDepartment());
        branch.setPhoneNo(request.getBranchPhoneNo());
        branch.setMobileNo(request.getMobileNo());
        branch.setIndustryType(request.getIndustryType());
        branch.setPrefectureCode(request.getPrefectureCode());

        Office office = officeRepository.findOne(request.getMedicalOfficeId());
        if (office.getContact() != null && StringUtils.isNotBlank(
                office.getContact().getPrefectureCode())) {
            mrUser.addAreaTypeItem(office.getContact().getPrefectureCode());
        }

        // Job type
        mrUser.setJobType(request.getJobType());

        branchRepository.save(branch);
        officeUserRepository.save(mrUser);
        identificationRepository.save(inIdentification);

        boolean isOfficeRestrictedMeeting = false;
        OfficeSettings officeSettings =
                officeSettingsRepository.findFirstByOfficeId(request.getMedicalOfficeId());
        if (officeSettings != null) {
            isOfficeRestrictedMeeting = officeSettings.isRestrictedMeeting();
        }
        // Handle offices
        if (StringUtils.isNotBlank(request.getMedicalOfficeId())) {
            // Check handlingHospital is existing
            HandlingHospital handlingHospital =
                    handlingHospitalRepository.findFirstByOfficeIdAndUserIdAndMrOfficeIdAndOtherHospital(
                            request.getMedicalOfficeId(), mrUser.getUserId(), mrUser.getOfficeId(),
                            false);
            if (handlingHospital == null) {
                Optional<MedicalOffice> maMedicalOffice =
                        officeRepository.findMedicalOffice(request.getMedicalOfficeId());

                handlingHospital = new HandlingHospital();
                handlingHospital.setUserId(mrUser.getUserId());
                handlingHospital.setMrOfficeId(mrUser.getOfficeId());
                handlingHospital.setOfficeId(request.getMedicalOfficeId());
                if (maMedicalOffice.isPresent()) {
                    MedicalOffice medicalOffice = maMedicalOffice.get();
                    handlingHospital.setOfficeNameKana(medicalOffice.getNameKana());
                    handlingHospital.setOfficeName(medicalOffice.getName());
                }
                handlingHospital.setOfficeUserId(mrUser.getId());
                handlingHospital.setIdentifyStatus(IdentifyStatus.UNCONFIRMED);
                handlingHospital.setOtherHospital(false);
                handlingHospital.setStart(Dates.now());
                // update CR#5722
                if (isOfficeRestrictedMeeting) {

                    handlingHospital.setRestriction(MeetingRestriction.CUSTOMER_UNREQUESTABLE);
                } else {
                    if (request.getIsRestrictedKeyCode()) {
                        handlingHospital.setRestriction(MeetingRestriction.CUSTOMER_UNREQUESTABLE);
                    } else {
                        handlingHospital.setRestriction(MeetingRestriction.NO_LIMIT);
                    }
                }

                handlingHospitalRepository.save(handlingHospital);

                // Update CR 6424, When MR connected to the hospital, MR could not see the message from the hospital in the past
                grpcClientCmsService.addOfficeInfoTarget(request.getMedicalOfficeId(), mrUser.getOfficeId(), mrUser.getId());
            }

            List<HandlingHospital> lsHandlingHospital = new ArrayList<>();
            lsHandlingHospital.add(handlingHospital);
            mrUser.setHandleOffices(lsHandlingHospital);
        }

        mrUser.setBranch(branch);
        mrUser.setProfile(profile);
        mrUser.setIdentification(inIdentification);
        mrUser.setManagementAuthority(ManagementAuthority.MP_3);
        mrUser.setFuncAuthority(FuncAuthoritySet.FPS_0);
        mrUser.setMailAddress(request.getMailAddress());
        mrUser.setAccountStatuses(AccountStatuses.fromAccountStatus(AccountStatus.VALID).getBits());
        Settings settings = commonService.settingNotification(false);
        mrUser.setSettings(settings);
        officeUserRepository.save(mrUser);

        // Update officeUserList for User
        List<OfficeUser> mrUserList = new ArrayList<>();
        mrUserList.add(mrUser);
        user.setOfficeUsers(mrUserList);
        userRepository.save(user);

        branchRepository.save(branch);
        // init the calendar data when register account
        calendarService.initDataWhenRegister(mrUser.getUserId(), mrUser.getOfficeId());

        RECreateUserResponse build =
                RECreateUserResponse.newBuilder()
                        .setOfficeId(mrUser.getOfficeId())
                        .setOfficeUserId(mrUser.getId())
                        .build();
        // send mail when finish create
        commonService.publishNotLogin(NotificationID.RE009, mrUser.getId());

        // create side menu
        SMCreateRequest.Builder menuRequest = SMCreateRequest.newBuilder();

        SMUser.Builder smUser = commonService.createSMUser(mrUser.getId(), SMUser.SMProductType.PR,
                mrUser.getManagementAuthority(), mrUser.getFuncAuthority());

        menuRequest.addUsers(smUser.build());
        grpcClientSideMenuService.create(menuRequest.build());
        grpcClientElasticService.updateUserProfile(mrUser, "");

        // update status token
        UserProvisionalEntry userProvisionalEntry = userProvisionalEntryRepository.findUserProvisionalEntryByEntryToken(request.getEntryToken());
        if (userProvisionalEntry != null) {
            userProvisionalEntry.setStatus(false);
            userProvisionalEntryRepository.save(userProvisionalEntry);
        }
        //update userInfo to firebase
        grpcClientSideMenuService.updateUserInfo(commonService.createUpdateUserInfoRequest(Collections.singletonList(mrUser)));
        return build;
    }

    /**
     * Get list user info
     *
     * @param request Get user request
     * @return GRPC User info
     */
    public REListUser getUserList(REGetUserListRequest request) {
        REListUser.Builder builder = REListUser.newBuilder();

        Set<String> listUserId = new HashSet<>();
        List<String> listOfficeId = new ArrayList<>();

        request.getListUserIdAndOfficeIdList().forEach(item -> {
            String userId = item.getUserId();
            String OfficeId = item.getOfficeId();
            listUserId.add(userId);
            listOfficeId.add(OfficeId);
        });

        // Get user in list userId
        List<User> user = userRepository.findAllByIdIn(listUserId);

        user.forEach(item -> {
            List<OfficeUser> officeUserList = new ArrayList<>();
            List<OfficeUser> officeUsers = item.getOfficeUsers();
            officeUsers.forEach(officeUser -> {
                if (listOfficeId.contains(officeUser.getOfficeId())) {
                    officeUserList.add(officeUser);
                }
            });

            REUserAndUserId.Builder reUserAndUserId = REUserAndUserId.newBuilder();

            // Deep copy
            if (!CollectionUtils.isEmpty(officeUserList)) {
                OfficeUser officeUser = officeUserList.get(0);

                REUser.Builder reBuilder = REUser.newBuilder();

                // set firstName
                reBuilder.setFirstName(Strings.nvl(officeUser.getFirstName()));

                // set lastName
                reBuilder.setLastName(Strings.nvl(officeUser.getLastName()));

                // set officeId
                reBuilder.setOfficeId(Strings.nvl(officeUser.getOfficeId()));

                reUserAndUserId.setReUser(reBuilder);
            }

            reUserAndUserId.setUserId(Strings.nvl(item.getId()));

            builder.addUser(reUserAndUserId);
        });

        return builder.build();
    }

    /**
     * Get user info
     *
     * @param request Get user request
     * @return GRPC User info
     */
    public REUser getUser(REGetUserRequest request, LoginInfo loginInfo) {
        REUser.Builder reUser = REUser.newBuilder();
        // Get user by userId
        User user = userRepository.findOne(request.getUserId());

        // Check user exist
        if (user == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_USER)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        List<OfficeUser> officeUserList = null;

        // Get officeUser by officeId
        List<OfficeUser> officeUsers = user.getOfficeUsers();
        if(officeUsers != null) {
            officeUserList = officeUsers.
                    stream().
                    filter(oUser -> oUser != null && oUser.getOfficeId().equals(request.getOfficeId())).
                    collect(Collectors.toList());
        }

        // Throw if not found office
        if (officeUserList == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_OFFICE)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        // Deep copy
        if (!CollectionUtils.isEmpty(officeUserList)) {
            OfficeUser officeUser = officeUserList.get(0);
            reUser.setOfficeType(getOfficeType(officeUser));
            setAuthorityReUser(officeUser, reUser);

            //REUser -> reDepartment
            Department department = departmentRepository.findOne(officeUser.getDepartmentId());
            if (department != null) {
                String displayName = department.getDisplayName();
                String departmentName = department.getName();
                REDepartment reDepartment = REDepartment.newBuilder()
                        .setId(department.getId())
                        .setDisplayName(displayName.isEmpty() ? departmentName : displayName)
                        .setName(departmentName.isEmpty() ? displayName : departmentName)
                        .setPath(department.getPath())
                        .build();
                reUser.setDepartment(reDepartment);
            }

            // REUser -> account status
            reUser.setAccountStatuses(officeUser.getAccountStatuses());
            FuncAuthoritySet funcAuthority = officeUser.getFuncAuthority();
            if (funcAuthority != null) {
                reUser.setFuncAuthoritySet(funcAuthority.asAuthority());
            }

            if (officeUser.getContact() != null) {
                Contact contact = officeUser.getContact();
                setContactReUser(contact, reUser);

                if (loginInfo != null) {
                    OfficeUser officeUserLogin =
                            officeUserRepository.findOne(loginInfo.getOfficeUserId());
                    ManagementAuthority managementAuthority =
                            officeUserLogin.getManagementAuthority();

                    if (contact.getMailAddressPublishingType() != null) {
                        if (PublishingType.ALL.name()
                                .equals(contact.getMailAddressPublishingType().name())) {
                            reUser.setMailAddress(Strings.nvl(user.getMailAddress()));
                        } else if (PublishingType.INHOUSE.name()
                                .equals(contact.getMailAddressPublishingType().name())) {
                            if (officeUser.getOfficeId().equals(officeUserLogin.getOfficeId())) {
                                reUser.setMailAddress(Strings.nvl(user.getMailAddress()));
                            }
                        } else if (PublishingType.PRIVATE.name()
                                .equals(contact.getMailAddressPublishingType().name())) {
                            if (officeUser.getUserId().equals(officeUserLogin.getUserId())) {
                                reUser.setMailAddress(Strings.nvl(user.getMailAddress()));
                            }
                        }
                    }

                    if (contact.getMobileNoPublishingType() != null) {
                        if (PublishingType.ALL.name()
                                .equals(contact.getMobileNoPublishingType().name())) {
                            reUser.setMobileNo(Strings.nvl(contact.getMobileNo()));
                        } else if (PublishingType.INHOUSE.name()
                                .equals(contact.getMobileNoPublishingType().name())) {
                            if (officeUser.getOfficeId().equals(officeUserLogin.getOfficeId())) {
                                reUser.setMobileNo(Strings.nvl(contact.getMobileNo()));
                            }
                        } else if (PublishingType.PRIVATE.name()
                                .equals(contact.getMobileNoPublishingType().name())) {
                            if (officeUser.getUserId().equals(officeUserLogin.getUserId())) {
                                reUser.setMobileNo(Strings.nvl(contact.getMobileNo()));
                            }
                        }
                    }
                } else {
                    if (contact.getMailAddressPublishingType() != null && !PublishingType.PRIVATE.name()
                            .equals(contact.getMailAddressPublishingType().name())) {
                        // set mail address
                        reUser.setMailAddress(Strings.nvl(user.getMailAddress()));
                    }

                    if (contact.getMobileNoPublishingType() != null && !PublishingType.PRIVATE.name()
                            .equals(contact.getMobileNoPublishingType().name())) {
                        reUser.setMobileNo(Strings.nvl(contact.getMobileNo()));
                    }
                }

            }
            // REUser -> jobType
            reUser.setJobType(Strings.nvl(officeUser.getJobType()));
            if (officeUser.getJobType() != null && !officeUser.getJobType().isEmpty()) {
                reUser.setJobName(
                        Strings.nvl(grpcClientMasterService.getJobName(officeUser.getJobType())));
            }

            reUser.addAllSpecializedDepartment(commonService.getSpecializedDepartment(officeUser));

            Profile profile = officeUser.getProfile();
            if (profile != null) {
                setProfileReUser(profile, reUser);
            }

            // set birthDate
            if (officeUser.getBirthDate() != null) {
                reUser.setBirthDate(Dates.formatUTC(officeUser.getBirthDate()));
            }
            //set officeUserId
            reUser.setOfficeUserId(Strings.nvl(officeUser.getId()));
            // set firstName
            reUser.setFirstName(Strings.nvl(officeUser.getFirstName()));

            // set firstNameKana
            reUser.setFirtNameKana(Strings.nvl(officeUser.getFirstNameKana()));

            // set lastName
            reUser.setLastName(Strings.nvl(officeUser.getLastName()));

            // set lastNameKana
            reUser.setLastNameKana(Strings.nvl(officeUser.getLastNameKana()));
            // set userId
            reUser.setUserId(Strings.nvl(officeUser.getUserId()));

            // set additionalMailAddress
            if (officeUser.getAdditionalMailAddresses() != null) {
                reUser.addAllAdditionalMailAddress(officeUser.getAdditionalMailAddresses());
            }
            // set officeId
            reUser.setOfficeId(Strings.nvl(officeUser.getOfficeId()));
            // get office with officeId
            Office office = officeRepository.findOne(officeUser.getOfficeId());
            if (office != null) {
                // set officeName
                reUser.setOfficeName(office.getName());
            }

            if (officeUser instanceof MRUser) {
                MRUser mrUser = MRUser.class.cast(officeUser);
                List<HandlingHospital> handlingHospitals = mrUser.getHandleOffices();
                if (!CollectionUtils.isEmpty(handlingHospitals)) {

                    handlingHospitals.forEach(handlingHospital -> {
                        REHandlingHospital.Builder builder = REHandlingHospital.newBuilder();

                        builder.setId(Strings.nvl(handlingHospital.getId()));
                        builder.setMrOfficeId(Strings.nvl(handlingHospital.getMrOfficeId()));
                        builder.setUserId(Strings.nvl(handlingHospital.getUserId()));

                        builder.setOfficeId(Strings.nvl(handlingHospital.getOfficeId()));
                        builder.setOfficeName(Strings.nvl(handlingHospital.getOfficeName()));
                        builder.setOtherHospital(handlingHospital.isOtherHospital());
                        builder.setOperator(Strings.nvl(handlingHospital.getOperator()));

                        reUser.addHandlingHospitals(builder);
                    });
                }
            }
        }

        // set login id
        reUser.setLoginId(Strings.nvl(user.getLoginId()));

        // set new login id
        reUser.setNewLoginId(StringUtils.EMPTY);

        reUser.setPersonalFlag(user.isPersonalFlag());

        reUser.setVerificationFlag(user.isVerificationFlag());
        if (loginInfo != null){
            StaffPin staffPin = staffPinRepository.findStaffPinByUserIdAndOfficeIdAndStaffUserIdAndStaffOfficeId(loginInfo.getUserId(),loginInfo.getOfficeId(),request.getUserId(),request.getOfficeId());
            if(staffPin != null){
                reUser.setPin(true);
            }else {
                reUser.setPin(false);
            }
        }

        return reUser.build();
    }

    /**
     * Get user for list user
     * @param officeUser
     * @param office
     * @param department
     * @param jobName
     * @param nameArenaMap
     * @param nameTypeMap
     * @return
     */
    public REUser getUserForListUser(OfficeUser officeUser, Office office, Department department, String jobName, String prefectureName,
                                     Map<String, String> nameArenaMap, Map<String, String> nameTypeMap) {
        REUser.Builder reUser = REUser.newBuilder();
        // Get user by userId
        User user = userRepository.findOne(officeUser.getUserId());

        reUser.setOfficeType(getOfficeType(officeUser));
        setAuthorityReUser(officeUser, reUser);

        if (department != null) {
            String displayName = department.getDisplayName();
            String departmentName = department.getName();
            REDepartment reDepartment = REDepartment.newBuilder()
                    .setId(department.getId())
                    .setDisplayName(displayName.isEmpty() ? departmentName : displayName)
                    .setName(departmentName.isEmpty() ? displayName : departmentName)
                    .setPath(department.getPath())
                    .build();
            reUser.setDepartment(reDepartment);
        }

        // REUser -> account status
        reUser.setAccountStatuses(officeUser.getAccountStatuses());
        FuncAuthoritySet funcAuthority = officeUser.getFuncAuthority();

        if (funcAuthority != null) {
            reUser.setFuncAuthoritySet(funcAuthority.asAuthority());
        }

        if (officeUser.getContact() != null) {
            Contact contact = officeUser.getContact();
            setContactReUser(contact, reUser);

            if (contact.getMailAddressPublishingType() != null && !PublishingType.PRIVATE.name()
                    .equals(contact.getMailAddressPublishingType().name())) {
                // set mail address
                reUser.setMailAddress(Strings.nvl(user.getMailAddress()));
            }

            if (contact.getMobileNoPublishingType() != null && !PublishingType.PRIVATE.name()
                    .equals(contact.getMobileNoPublishingType().name())) {
                reUser.setMobileNo(Strings.nvl(contact.getMobileNo()));
            }

        }
        // REUser -> jobType
        reUser.setJobType(Strings.nvl(officeUser.getJobType()));
        reUser.setJobName(Strings.nvl(jobName));


        // REUser -> specializedDepartment
        List<RESpecializedDepartment> reSpecializedDepartments = new ArrayList<>();

        List<SpecializedDepartment> specializedDepartmentList = officeUser.getSpecializedDepartments();
        specializedDepartmentList.forEach(specializedDepartment -> {
            RESpecializedDepartment.Builder reSpecializedDepartment =
                    RESpecializedDepartment.newBuilder();

            // set name field
            if (specializedDepartment.getFieldId() != null) {
                reSpecializedDepartment.setFieldId(Strings.nvl(specializedDepartment.getFieldId()));
                reSpecializedDepartment.setNameField(Strings.nvl(nameArenaMap.get(specializedDepartment.getFieldId())));
            }

            //set Specialized with type
            if (specializedDepartment.getTypeId() != null) {
                reSpecializedDepartment.setTypeId(Strings.nvl(specializedDepartment.getTypeId()));
                reSpecializedDepartment.setNameType(Strings.nvl(nameTypeMap.get(specializedDepartment.getTypeId())));
            }
            // set name type
            reSpecializedDepartments.add(reSpecializedDepartment.build());
        });

        reUser.addAllSpecializedDepartment(reSpecializedDepartments);

        Profile profile = officeUser.getProfile();
        if (profile != null) {
            setProfileReUser(profile, reUser);
        }

        // set birthDate
        if (officeUser.getBirthDate() != null) {
            reUser.setBirthDate(Dates.formatUTC(officeUser.getBirthDate()));
        }
        //set officeUserId
        reUser.setOfficeUserId(Strings.nvl(officeUser.getId()));
        //set userId
        reUser.setUserId(Strings.nvl(officeUser.getUserId()));
        // set firstName
        reUser.setFirstName(Strings.nvl(officeUser.getFirstName()));

        // set firstNameKana
        reUser.setFirtNameKana(Strings.nvl(officeUser.getFirstNameKana()));

        // set lastName
        reUser.setLastName(Strings.nvl(officeUser.getLastName()));

        // set lastNameKana
        reUser.setLastNameKana(Strings.nvl(officeUser.getLastNameKana()));

        // set additionalMailAddress
        if (officeUser.getAdditionalMailAddresses() != null) {
            reUser.addAllAdditionalMailAddress(officeUser.getAdditionalMailAddresses());
        }
        // set officeId
        reUser.setOfficeId(Strings.nvl(officeUser.getOfficeId()));
        // get office with officeId
        if (office != null) {
            // set officeName
            reUser.setOfficeName(office.getName());
        }

        if (officeUser instanceof MRUser) {
            MRUser mrUser = MRUser.class.cast(officeUser);
            Branch branch = mrUser.getBranch();
            List<HandlingHospital> handlingHospitals = mrUser.getHandleOffices();
            if (!CollectionUtils.isEmpty(handlingHospitals)) {

                handlingHospitals.forEach(handlingHospital -> {
                    REHandlingHospital.Builder builder = REHandlingHospital.newBuilder();

                    builder.setId(Strings.nvl(handlingHospital.getId()));
                    builder.setMrOfficeId(Strings.nvl(handlingHospital.getMrOfficeId()));
                    builder.setUserId(Strings.nvl(handlingHospital.getUserId()));

                    builder.setOfficeId(Strings.nvl(handlingHospital.getOfficeId()));
                    builder.setOfficeName(Strings.nvl(handlingHospital.getOfficeName()));
                    builder.setOtherHospital(handlingHospital.isOtherHospital());
                    builder.setOperator(Strings.nvl(handlingHospital.getOperator()));

                    reUser.addHandlingHospitals(builder);
                });
            }

            reUser.setExperience(mrUser.getExperience());
            if (CollectionUtils.isNotEmpty(mrUser.getHandleFields())) {
                reUser.addAllHandleFields(mrUser.getHandleFields());
            }
            REBranch.Builder reBranchBuilder = REBranch.newBuilder();
            if (branch != null) {
                reBranchBuilder.setName(Strings.nvl(branch.getName()));
                reBranchBuilder.setAddress(Strings.nvl(branch.getAddress()));
                reBranchBuilder.setPhoneNo(Strings.nvl(branch.getPhoneNo()));
                reBranchBuilder.setMobileNo(Strings.nvl(branch.getMobileNo()));
                reBranchBuilder.setIndustryType(Strings.nvl(branch.getIndustryType()));
                reBranchBuilder.setDepartment(Strings.nvl(branch.getDepartment()));
                reBranchBuilder.setPrefectureCode(Strings.nvl(branch.getPrefectureCode()));
                reUser.setBranch(reBranchBuilder);
            }
        }

        // set login id
        reUser.setLoginId(Strings.nvl(user.getLoginId()));

        // set new login id
        reUser.setNewLoginId(StringUtils.EMPTY);

        reUser.setPersonalFlag(user.isPersonalFlag());

        reUser.setVerificationFlag(user.isVerificationFlag());

        reUser.setPrefectureName(Strings.nvl(prefectureName));

        return reUser.build();
    }

    /**
     * <p> Update user RE0016, RE0025</p>
     *
     * @param request the information of User
     */
    public REUserResponse putUser(REUser request, LoginInfo loginInfo) {

        REUserResponse.Builder builder = REUserResponse.newBuilder();
        // Check user exist
        User user = userRepository.findFirstByLoginId(request.getLoginId());
        if (user == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_USER)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        String officeId = loginInfo.getOfficeId();
        // office users
        OfficeUser officeUser = null;
        for (OfficeUser item : user.getOfficeUsers()) {
            if (item.getOfficeId().equals(officeId)) {
                officeUser = item;
                break;
            }
        }

        if (officeUser == null) {
            throw ServiceStatus.NOT_FOUND
                    .withMessage(NOT_FOUND_OFFICE)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }
        builder.setOfficeUserId(officeUser.getId());
        // Detect first entry flag if user update self-info
        boolean firstEntry = false;
        EnumSet<FuncAuthority> enumSetFuncAuthority = officeUser.getFuncAuthority().getAuthorities().clone();

        // Get user info if login user is Admin
        Optional<OfficeUser> officeUserOpt = officeUserRepository.findOfficeUser(loginInfo.getUserId(), loginInfo.getOfficeId());
        if (officeUserOpt.isPresent()){
            OfficeUser admin = officeUserOpt.get();

            if (admin.getLoginId().equals(request.getLoginId())
                    && AccountStatuses.fromAccountStatus(AccountStatus.PROVISIONAL).getBits() == officeUser.getAccountStatuses()){
                firstEntry = true;
            }
        }

        // Old deptId
        String oldDeptId = Strings.nvl(officeUser.getDepartmentId());

        // Check update department when edit user info
        String deptId = request.getDepartment().getId();

        // Get current department
        Department department = null;

        if (StringUtils.isNotBlank(deptId) && !deptId.equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            department = departmentRepository.findOne(deptId);
            if (department != null) {
                // call shift service to update data
                if(!deptId.equals(oldDeptId)){
                    grpcClientShiftService.updateShiftDateWhenChangeDept(SHUpdateShiftDataWhenChangeDept.newBuilder()
                                                                                 .setOfficeUserId(Strings.nvl(officeUser.getId()))
                                                                                 .addOldDepartmentId(Strings.nvl(oldDeptId))
                                                                                 .setNewDepartmentId(Strings.nvl(deptId))
                                                                                 .build());
                    grpcClientAttendanceService.handleUpdatingAttendanceData(ATUpdatedDepartment.newBuilder()
                                                                                     .setOfficeUserId(Strings.nvl(officeUser.getId()))
                                                                                     .addOldDepartmentIds(Strings.nvl(oldDeptId))
                                                                                     .setNewDepartmentId(Strings.nvl(deptId))
                                                                                     .build());
                }

                officeUser.setPath(department.getPath());
                officeUser.setDepartmentId(request.getDepartment().getId());
            }
        } else {
            department = departmentRepository.findOne(officeUser.getDepartmentId());
        }

        if (department == null) {
            throw ServiceStatus.INVALID_ARGUMENT
                    .withMessage(NOT_FOUND_DEPARTMENT)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        boolean isMenu = false;
        REManagementAuthority reManagementAuthority = request.getManagementAuthority();

        boolean currentFP7Role = false;
        if (officeUser.getFuncAuthority() != null) {
            currentFP7Role = officeUser.getFuncAuthority().asAuthorities().getFP7();
        }

        if (reManagementAuthority.compareTo(REManagementAuthority.UNRECOGNIZED) != 0
                && officeUser.getManagementAuthority()
                .name()
                .equals(ManagementAuthority.MP_1.name())
                && !reManagementAuthority.name()
                .equals(officeUser.getManagementAuthority().name()) && officeUser.getAccountStatuses() == 2) {

            commonService.checkAdminOffice(officeUser);
        }

        // set manage authority
        if (reManagementAuthority.compareTo(REManagementAuthority.UNRECOGNIZED) != 0
                && officeUser.getManagementAuthority() != null
                && !reManagementAuthority.name()
                .equals(officeUser.getManagementAuthority().name())) {
            officeUser.setManagementAuthority(
                    ManagementAuthority.atCode(reManagementAuthority.getNumber()));
            isMenu = true;
        }

        // set ManagementLevel
        //#3075
        REManagementLevel reManagementLevel = request.getFp3ManagementLevel();
        if (reManagementLevel.compareTo(REManagementLevel.UNRECOGNIZED) != 0) {
            officeUser.setFp3ManagementLevel(ManagementLevel.valueOf(reManagementLevel.name()));
        }
        // set Attendance management level
        REManagementLevel fp12ManagementLevel = request.getFp12ManagementLevel();
        if (fp12ManagementLevel.compareTo(REManagementLevel.UNRECOGNIZED) != 0) {
            officeUser.setFp12ManagementLevel(ManagementLevel.valueOf(fp12ManagementLevel.name()));
        }

        REManagementLevel fp15ManagementLevel = request.getFp15ManagementLevel();
        if (fp15ManagementLevel.compareTo(REManagementLevel.UNRECOGNIZED) != 0) {
            officeUser.setFp15ManagementLevel(ManagementLevel.valueOf(fp15ManagementLevel.name()));
        }
        // set contact
        setContactUser(request, officeUser);

        // set profile
        Profile profile = officeUser.getProfile();
        boolean setData = false;
        if (profile == null) {
            profile = new Profile();
        }

        if (!request.getImageUrl().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            profile.setImage(request.getImageUrl());
            setData = true;
        }

        if (!request.getHobby().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            profile.setHobby(request.getHobby());
            setData = true;
        }

        if (!request.getPlaceBornIn().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            profile.setPlaceBornIn(request.getPlaceBornIn());
            setData = true;
        }

        if (!request.getQualification().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            profile.setQualification(request.getQualification());
            setData = true;
        }

        if (!request.getPosition().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            profile.setPosition(request.getPosition());
            setData = true;
        }

        if (!request.getBriefHistory().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            profile.setBriefHistory(request.getBriefHistory());
            setData = true;
        }

        if (!request.getGraduationDate().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            try {
                Date date = Dates.parseISO(request.getGraduationDate() + TIME_ZONE);
                profile.setGraduationDate(date);
                setData = true;
            } catch (IllegalArgumentException e) {
                throw ServiceStatus.INVALID_ARGUMENT
                        .withMessage(DATE_INVALID_FORMAT)
                        .addError(Message.COMMON_SAVE_FAILED)
                        .asStatusRuntimeException();
            }
        }
        if (request.getGender() != null
                && request.getGender().compareTo(REGender.UNRECOGNIZED) != 0) {
            GenderType genderType = GenderType.convertGenderType(request.getGenderValue());
            profile.setGenderType(genderType);
            setData = true;
        }

        if (!request.getJobType().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            officeUser.setJobType(request.getJobType());
        }
        commonService.saveSpecializedDepartment(officeUser, request.getSpecializedDepartmentList());

        if (request.getDepartment() != null && (!request.getDepartment()
                .equals(REDepartment.getDefaultInstance()))) {
            officeUser.setDepartmentId(request.getDepartment().getId());
        }

        if (!request.getBirthDate().isEmpty() && !request.getBirthDate()
                .equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            try {
                Date date = Dates.parseISO(request.getBirthDate() + TIME_ZONE);
                officeUser.setBirthDate(date);
            } catch (IllegalArgumentException e) {
                throw ServiceStatus.INVALID_ARGUMENT
                        .withMessage(DATE_INVALID_FORMAT)
                        .addError(Message.COMMON_SAVE_FAILED)
                        .asStatusRuntimeException();
            }
        }

        if (!request.getFirstName().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            officeUser.setFirstName(request.getFirstName());
        }

        if (!request.getFirtNameKana().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            officeUser.setFirstNameKana(request.getFirtNameKana());
        }

        if (!request.getLastName().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            officeUser.setLastName(request.getLastName());
        }

        if (!request.getLastNameKana().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            officeUser.setLastNameKana(request.getLastNameKana());
        }

        if (!request.getPassword().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            user.setPassword(encryptProxy.encrypt(request.getPassword()));
            user.setTemporaryPassword(null);
        }

        boolean initDataCalendar = false;
        int accountStatuses = request.getAccountStatuses();

        if (accountStatuses != 0
                && accountStatuses != officeUser.getAccountStatuses()) {

            if (accountStatuses == AccountStatuses.fromAccountStatus(
                    AccountStatus.VALID).getBits()
                    && officeUser.getAccountStatuses() == AccountStatuses.fromAccountStatus(
                    AccountStatus.PROVISIONAL).getBits()) {
                // TODO: remove when AMQP is done
                // init the calendar data when register account
                initDataCalendar = true;

                // Active account
                officeUser.setAccountStatuses(
                        AccountStatuses.fromAccountStatus(AccountStatus.VALID).getBits());
            } else {
                // Change accountStatus in other cases
                officeUser.setAccountStatuses(accountStatuses);
            }
        }

        if (setData) {
            profileRepository.save(profile);
            officeUser.setProfile(profile);
        }
        // fix bug #14151
        Map<String,String> errorList = new HashMap<>();
        boolean isLogin = false ;
        // check exist loginId
        if (StringUtils.isNotBlank(request.getNewLoginId())
                && !REQUEST_PARAM_DEFAULT_VALUE.equalsIgnoreCase(request.getNewLoginId())
                && !request.getNewLoginId().equals(user.getLoginId())) {
            if (commonService.isUserExist(request.getNewLoginId(),officeUser.getId())) {
                errorList.put("loginId",request.getNewLoginId());
            }else {
                user.setLoginId(request.getNewLoginId());
                officeUser.setLoginId(request.getNewLoginId());
                isLogin = true ;
            }
        }

        if (!REQUEST_PARAM_DEFAULT_VALUE.equals(request.getMailAddress())
                && StringUtils.isNotBlank(request.getMailAddress())
                && !request.getMailAddress().equals(officeUser.getMailAddress())
                && commonService.isExistMail(
                request.getMailAddress(), officeUser.getId())) {
            errorList.put("mailAddress",request.getMailAddress());
        }

        commonService.checkMailErrorAddition(errorList,request.getAdditionalMailAddressList(),
                                             request.getMailAddress(),
                                             officeUser);

        if (!errorList.isEmpty()){
            String message = new Gson().toJson(errorList);
            throw ServiceStatus.NOT_FOUND.withMessage(message)
                    .addError(Message.RE0020_E003_1).asStatusRuntimeException();
        }

        // set AdditionalMailAddress
        if (request.getAdditionalMailAddressList().isEmpty()) {
            officeUser.setAdditionalMailAddresses(Lists.newArrayList());
        } else if (!request.getAdditionalMailAddressList().get(0).equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            commonService.updateMailAdditionAddress(request.getAdditionalMailAddressList(),
                                                    request.getMailAddress(),
                                                    officeUser);
        }

        // check exist email
        if (!REQUEST_PARAM_DEFAULT_VALUE.equals(request.getMailAddress())
                && !request.getMailAddress().equals(officeUser.getMailAddress())) {
            commonService.updateMailAddress(user, officeUser, request.getMailAddress(), firstEntry);
        }

        if (isLogin && StringUtils.isNotBlank(request.getNewLoginId())&&  ValidateUtils.isEmail(request.getNewLoginId())) {
            commonService.publish(NotificationID.RE006, request.getNewLoginId());
        }
        REFuncAuthoritySet reFuncAuthoritySet = request.getFuncAuthoritySet();

        boolean newFP7Role = false;
        boolean deleteMeetingRole = false;
        boolean deleteMediatorRole = false;
        boolean addMediatorRole = false;

        if (reFuncAuthoritySet.compareTo(REFuncAuthoritySet.UNRECOGNIZED) != 0) {
            FuncAuthoritySet funcAuthoritySet =
                    FuncAuthoritySet.atCode(reFuncAuthoritySet.getNumber());
            if (reFuncAuthoritySet.compareTo(REFuncAuthoritySet.FPS_0) == 0) {
                REFuncAuthority reFuncAuthority = request.getFuncAuthority();
                EnumSet<FuncAuthority> funcAuthorities = EnumSet.of(FuncAuthority.FP_1);
                LOGGER.debug("fp12 funcAuthority: " + reFuncAuthority.getFP12());
                if (!reFuncAuthority.getFP1()) {
                    funcAuthorities.remove(FuncAuthority.FP_1);

                    if (enumSetFuncAuthority.contains(FuncAuthority.FP_1)) {
                        deleteMeetingRole = true;
                    }
                }

                if (reFuncAuthority.getFP2()) {
                    funcAuthorities.add(FuncAuthority.FP_2);
                }

                if (reFuncAuthority.getFP3()) {
                    funcAuthorities.add(FuncAuthority.FP_3);
                }

                if (reFuncAuthority.getFP4()) {
                    funcAuthorities.add(FuncAuthority.FP_4);
                }

                if (reFuncAuthority.getFP5()) {
                    funcAuthorities.add(FuncAuthority.FP_5);
                }

                if (reFuncAuthority.getFP6()) {
                    funcAuthorities.add(FuncAuthority.FP_6);
                }

                if (reFuncAuthority.getFP7()) {
                    funcAuthorities.add(FuncAuthority.FP_7);
                    newFP7Role = true;

                    // Add the mediator role
                    if (!enumSetFuncAuthority.contains(FuncAuthority.FP_7)) {
                        addMediatorRole = true;
                    }
                } else {
                    // Delete the mediator role
                    if (enumSetFuncAuthority.contains(FuncAuthority.FP_7)) {
                        deleteMediatorRole = true;
                    }
                }

                if (reFuncAuthority.getFP8()) {
                    funcAuthorities.add(FuncAuthority.FP_8);
                }

                if (reFuncAuthority.getFP9()) {
                    funcAuthorities.add(FuncAuthority.FP_9);
                }

                // dangnh-java add FP-10 for shift
                if (reFuncAuthority.getFP10()) {
                    funcAuthorities.add(FuncAuthority.FP_10);
                }
                if (reFuncAuthority.getFP12()) {
                    funcAuthorities.add(FuncAuthority.FP_12);
                }

                if (reFuncAuthority.getFP15()) {
                    funcAuthorities.add(FuncAuthority.FP_15);
                }

                if (reFuncAuthority.getFP16()) {
                    funcAuthorities.add(FuncAuthority.FP_16);
                }

                // update authorities
                funcAuthoritySet = FuncAuthoritySet.FPS_0.updateAuthority(funcAuthorities);
                officeUser.setFuncAuthority(funcAuthoritySet);
                LOGGER.debug("put User input value"+officeUser.getFuncAuthority().getAuthorities().clone() );
                officeUserRepository.save(officeUser);
            } else {
                EnumSet<FuncAuthority> funcAuthorities = funcAuthoritySet.getAuthorities();
                if (funcAuthorities.contains(FuncAuthority.FP_7)) {
                    newFP7Role = true;
                }

                if (enumSetFuncAuthority.contains(FuncAuthority.FP_1) && !funcAuthorities.contains(FuncAuthority.FP_1)) {
                    deleteMeetingRole = true;
                }

                // Add the mediator role
                if (!enumSetFuncAuthority.contains(FuncAuthority.FP_7) && funcAuthorities.contains(FuncAuthority.FP_7)) {
                    addMediatorRole = true;
                }

                // Delete the mediator role
                if (enumSetFuncAuthority.contains(FuncAuthority.FP_7) && !funcAuthorities.contains(FuncAuthority.FP_7)) {
                    deleteMediatorRole = true;
                }

                officeUser.setFuncAuthority(funcAuthoritySet);
                officeUserRepository.save(officeUser);
            }

            isMenu = true;
        } else {
            if (officeUser.getFuncAuthority() != null && officeUser.getFuncAuthority().equals(FuncAuthoritySet.FPS_0)) {
                FuncAuthoritySet funcAuthoritySet = officeUser.getFuncAuthority().updateAuthority(enumSetFuncAuthority);
                officeUser.setFuncAuthority(funcAuthoritySet);
                officeUserRepository.save(officeUser);
            } else {
                officeUserRepository.save(officeUser);
            }
        }

        // If change the mediator role then call to service-meeting to reset the meeting-side-menu
        if ((OfficeType.MEDICAL.name().equalsIgnoreCase(officeUser.getOfficeType().name())
                || OfficeType.DRUG_STORE.name().equalsIgnoreCase(officeUser.getOfficeType().name())
        ) && currentFP7Role != newFP7Role) {
            grpcClientMeetingService.resetMeetingSideMenu(officeUser.getUserId(), officeUser.getOfficeId());
        }

        // add profile
        userRepository.save(user);

        grpcClientElasticService.updateUserProfile(officeUser, department.getPath());

        // TODO: remove when AMQP is done
        // init the calendar data when register account
        if (initDataCalendar) {
            try {
                calendarService.initDataWhenRegister(officeUser.getUserId(),
                                                     officeUser.getOfficeId());
            } catch (Exception ex) {
                LOGGER.warn(ex.getMessage());
            }
            // Send mail first registration finish
            if (StringUtils.isNotBlank(officeUser.getMailAddress())) {
                commonService.publish(NotificationID.RE002, officeUser.getMailAddress());
            }
        }

        // Fix bug 17391: Cancel meeting of DR
        if(deleteMeetingRole) {
            grpcClientMeetingService.changeStatusMeetingRequest(officeUser.getUserId(),
                                                                officeUser.getOfficeId(),
                                                                "",
                                                                "",
                                                                "",
                                                                null,
                                                                MEActionChangeStatus.DR_DELETED_MEETING_ROLE);

            // CR #11667: Delete MrShareInfoStatus
            MrShareInfoStatusDeletionDTO mrShareInfoStatusDeletionDTO = new MrShareInfoStatusDeletionDTO();
            mrShareInfoStatusDeletionDTO.setReason(MrShareInfoStatusDeletionReason.DR);
            mrShareInfoStatusDeletionDTO.setDrOfficeUserId(officeUser.getId());
            mrShareInfoStatusService.deleteMrShareInfoStatus(mrShareInfoStatusDeletionDTO);
        }

        // Update seen presentation request when valid account
        if(firstEntry){
            grpcClientPresentationService.updateSeenPresentationRequestWhenValidAccount(officeUser.getId(), officeUser.getUserId(), officeUser.getOfficeId());
        }

        // Get all doctor that user is mediator in the case deleteMediatorRole = true or addMediatorRole = true
        List<String> listAllDoctorOfMediator = null;
        if (deleteMediatorRole || addMediatorRole) {
            listAllDoctorOfMediator = getDoctorsOfMediator(officeId, officeUser);
        }

        grpcClientRtmService.update(
                commonService.createRTUser(officeUser, deleteMeetingRole, addMediatorRole, deleteMediatorRole,
                                           listAllDoctorOfMediator));

        if (isMenu) {
            Settings settings = officeUser.getSettings();
            if (settings != null){
                SMUser.SMProductType productType;
                switch (officeUser.getOfficeType()) {
                    case MEDICAL:
                        productType = SMUser.SMProductType.DR;
                        break;
                    case DRUG_STORE:
                        productType = SMUser.SMProductType.PH;
                        break;
                    default:
                        productType = SMUser.SMProductType.DR;
                        break;
                }

                SMUser.Builder smUser = commonService.createSMUser(
                        officeUser.getId(), productType,
                        officeUser.getManagementAuthority(),
                        officeUser.getFuncAuthority()
                                                                  );
                SideMenuSettings sideMenuSettings = settings.getSideMenuSettings() ;
                if (sideMenuSettings != null){
                    List<MenuItem> listMenuItem = sideMenuSettings.getMenuItems();

                    listMenuItem.sort(Comparator.comparing(MenuItem::getDisplayOrder));
                    // update menu when update setting
                    SMUpdateRequest.Builder menuRequest = SMUpdateRequest.newBuilder();

                    SMUpdateOrderRequest.Builder oderRequest = SMUpdateOrderRequest.newBuilder();

                    List<SMUpdateOrderRequest.SMMenuItem> smMenuItems = new ArrayList<>();

                    for (MenuItem item : listMenuItem){
                        if (FUNCTIONID_4.equals(item.getFunctionId())){
                            smMenuItems.add(SMUpdateOrderRequest.SMMenuItem.forNumber(1));
                        }else if(FUNCTIONID_5.equals(item.getFunctionId())){
                            smMenuItems.add(SMUpdateOrderRequest.SMMenuItem.forNumber(0));
                        }else if(FUNCTIONID_6.equals(item.getFunctionId())){
                            smMenuItems.add(SMUpdateOrderRequest.SMMenuItem.forNumber(2));
                        }else if (FUNCTIONID_7.equals(item.getFunctionId())){
                            smMenuItems.add(SMUpdateOrderRequest.SMMenuItem.forNumber(3));
                        }
                    }

                    oderRequest.addAllItem(smMenuItems);
                    oderRequest.setUser(smUser.build());
                    menuRequest.setOrder(oderRequest);

                    grpcClientSideMenuService.update(menuRequest.build());
                } else {

                    SMUpdateRequest.Builder menuRequest = SMUpdateRequest.newBuilder();

                    SMUpdateUserRequest.Builder userRequest = SMUpdateUserRequest.newBuilder();

                    userRequest.addUsers(smUser.build());
                    menuRequest.setUser(userRequest);
                    grpcClientSideMenuService.update(menuRequest.build());
                }

            }
        }

        // update user info fire-base
        commonService.updateFIRUserAndGroupInfo(departmentService,department, oldDeptId, user.getId(), officeUser, firstEntry);
        //update accountStatus in attendance-sync
        grpcClientAttendanceSyncService.updateAccountStatus(officeUser.getOfficeId(),officeUser.getId(),officeUser.getAccountStatuses());

        countRequestAttendanceWhenChangeFuncAuthority(officeUser);

        return builder.build();
    }

    private void countRequestAttendanceWhenChangeFuncAuthority(OfficeUser officeUser) {
        Map<String, OfficeUserIdsInDept> mapListUserInDept = new HashMap<>();
        boolean hasFp15 = officeUser.getFuncAuthority().getAuthorities().contains(FuncAuthority.FP_15);
        boolean fp15ManagementAll = hasFp15 && officeUser.getFp15ManagementLevel() == ManagementLevel.All;
        boolean fp15ManagementLimited = hasFp15 && officeUser.getFp15ManagementLevel() == ManagementLevel.Limited;
        boolean hasFp16 = officeUser.getFuncAuthority().getAuthorities().contains(FuncAuthority.FP_16);

            if (fp15ManagementAll) {
                mapListUserInDept.put(officeUser.getId(), OfficeUserIdsInDept.newBuilder()
                        .addOfficeUserId(ALL_STAFF)
                        .build());
            } else {
                if (fp15ManagementLimited) {
                    List<String> deptIds = new ArrayList<>();
                    Department department = departmentRepository.findOne(officeUser.getDepartmentId());
                    getAllListDeptId(deptIds, department);

                    List<OfficeUser> officeUsers = officeUserRepository.findAllByDepartmentIdIn(deptIds);

                    List<String> officeUserIdInDept = officeUsers
                            .stream()
                            .map(OfficeUser::getId)
                            .collect(Collectors.toList());

                    if (CollectionUtils.isNotEmpty(officeUserIdInDept)) {
                        mapListUserInDept.put(officeUser.getId(), OfficeUserIdsInDept.newBuilder()
                                .addAllOfficeUserId(officeUserIdInDept)
                                .build());
                    }
                }
                // user has fp 16
                if (hasFp16) {
                    ATStaffRequestItem staffRequestItem = grpcClientAttendanceService
                            .getListStaffRequestByManagerRequestId(officeUser.getOfficeId(), officeUser.getId());

                    if (CollectionUtils.isNotEmpty(staffRequestItem.getStaffRequestIdList())) {
                        List<String> listOfficeUserId = new ArrayList<>();
                        // in case user has fp15 limit and fp16
                        if (mapListUserInDept.get(officeUser.getId()) != null ) {
                            listOfficeUserId.addAll(mapListUserInDept.get(officeUser.getId()).getOfficeUserIdList());
                        }
                        listOfficeUserId.addAll(staffRequestItem.getStaffRequestIdList());
                        mapListUserInDept.put(officeUser.getId(), OfficeUserIdsInDept.newBuilder()
                                .addAllOfficeUserId(listOfficeUserId)
                                .build());
                    }
                }
            }


        ATRequestResult requestResult = grpcClientAttendanceService.countRequestNewAndConfirm(officeUser.getOfficeId(),
                Collections.singletonList(officeUser.getId()),
                mapListUserInDept);

        List<UserRequestNumber> userRequestNumberList = new ArrayList<>();
        requestResult.getRequestCountList().forEach(requestCount -> {
            UserRequestNumber.Builder userRequestNumber = UserRequestNumber.newBuilder()
                    .setOfficeUserId(requestCount.getOfficeUserId())
                    .setNewConfirmedRequests(requestCount.getNewConfirmedRequest())
                    .setNewIncomingRequests(requestCount.getNewIncomingRequest());
            userRequestNumberList.add(userRequestNumber.build());
        });

        FBAtRequestNumber request = FBAtRequestNumber.newBuilder()
                .addAllUserRequestNumber(userRequestNumberList)
                .build();

        grpcClientSideMenuService.updateAttendanceRequestNumbers(request);
    }

    private void getAllListDeptId(List<String> deptIds, Department department) {
        deptIds.add(department.getId());
        if (!DEPARTMENT_ROOT.equals(department.getPath())) {
            department.getChildren().forEach(item -> getAllListDeptId(deptIds, item));
        }
    }

    public List<String>  getDoctorsOfMediator(String officeId, OfficeUser officeUser) {
        List<String> listAllDoctorOfMediator = new ArrayList<>();
        MEGetMediatedDoctorResponse mediatedDoctorResponse =
                grpcClientMeetingService.getMediatedDoctor(officeUser.getUserId(), officeUser.getOfficeId());

        if (mediatedDoctorResponse != null) {
            List<MediatedDoctorItem> listMediatedDoctorItem = mediatedDoctorResponse.getMediatorsList();

            if (!CollectionUtils.isEmpty(listMediatedDoctorItem)) {
                List<String> listDoctorUserIds = new ArrayList<>();
                for (MediatedDoctorItem mediatedDoctorItem: listMediatedDoctorItem) {
                    listDoctorUserIds.add(mediatedDoctorItem.getUserId());
                }

                // Get officeUserId of doctor by userId, officeId
                List<OfficeUser> listDoctor = officeUserRepository.getIdsByUserIdsAndOfficeId(listDoctorUserIds, officeId);

                for (MediatedDoctorItem mediatedDoctorItem: listMediatedDoctorItem) {
                    OfficeUser oUser = listDoctor.stream().filter((offUser)
                            -> offUser.getUserId().equalsIgnoreCase(mediatedDoctorItem.getUserId())
                            && offUser.getOfficeId().equalsIgnoreCase(mediatedDoctorItem.getOfficeId()))
                            .findAny().get();

                    listAllDoctorOfMediator.add(oUser.getId());
                }
            }
        }

        return listAllDoctorOfMediator;
    }

    /**
     * <p> Delete user </p>
     *
     * @param request contains userId
     * @param loginInfo contain login info of user logined
     */
    public void deleteUser(REDeleteUserRequest request, LoginInfo loginInfo) {
        deleteUser(request.getUserId(), loginInfo);
    }

    /**
     * Delete list user
     * @param request
     * @param loginInfo
     */
    public void deleteListUser(REDeleteListUserRequest request, LoginInfo loginInfo) {
        request.getUserIdsList().forEach(userId -> {
            deleteUser(userId, loginInfo);
        });

    }

    private void deleteUser(String userId, LoginInfo loginInfo){
        // Check user exist
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_USER)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        List<OfficeUser> officeUsers = user.getOfficeUsers();
        if (CollectionUtils.isEmpty(officeUsers)) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_OFFICE)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        OfficeUser officeUser = officeUsers.stream().filter(offUser ->
                loginInfo.getOfficeId().equals(offUser.getOfficeId())).findFirst().orElse(null);

        if(officeUser == null){
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_OFFICE)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        if (OfficeType.MEDICAL.equals(officeUser.getOfficeType()) || OfficeType.DRUG_STORE.equals(officeUser.getOfficeType())) {
            // Check admin group
            GRCheckMemberIsOnlyAdminResponse grCheckMemberIsOnlyAdminResponse = grpcClientGroupService.checkMemberIsAdmin(officeUser.getId());
            if (grCheckMemberIsOnlyAdminResponse.getIsOnlyAdmin()) {
                throw ServiceStatus.NOT_FOUND.withMessage(RE0020_E001)
                        .addError(RE0020_E001).asStatusRuntimeException();
            }

            // Check admin chat room
            RTCheckIsExclusivelyAdminResponse rtCheckIsExclusivelyAdminResponse = grpcClientRtmService.checkIsExclusivelyAdmin(officeUser.getId());
            if (rtCheckIsExclusivelyAdminResponse.getIsExclusivelyAdmin()) {
                throw ServiceStatus.NOT_FOUND.withMessage(RE0020_E002)
                        .addError(RE0020_E002).asStatusRuntimeException();
            }
        }

        // アカウント削除
        officeUser.invalidStatus();
        officeUserRepository.save(officeUser);
        user.invalidStatus();
        userRepository.save(user);

        // call meeting to change status of meeting request
        if (OfficeType.MEDICAL.equals(officeUser.getOfficeType()) || OfficeType.DRUG_STORE.equals(officeUser.getOfficeType())) {
            //call meeting to change status of meeting request
            grpcClientMeetingService
                    .changeStatusMeetingRequest(officeUser.getUserId(),
                            officeUser.getOfficeId(), Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, null, MEActionChangeStatus.DR_DELETED);

            // Delete MrShareInfoStatus
            MrShareInfoStatusDeletionDTO mrShareInfoStatusDeletionDTO = new MrShareInfoStatusDeletionDTO();
            mrShareInfoStatusDeletionDTO.setReason(MrShareInfoStatusDeletionReason.DR);
            mrShareInfoStatusDeletionDTO.setDrOfficeUserId(officeUser.getId());
            mrShareInfoStatusService.deleteMrShareInfoStatus(mrShareInfoStatusDeletionDTO);
        } else if (OfficeType.PHARMACY.equals(officeUser.getOfficeType())) {
            //call meeting to change status of meeting request
            grpcClientMeetingService
                    .changeStatusMeetingRequest(Strings.EMPTY, Strings.EMPTY, officeUser.getUserId(),
                            officeUser.getOfficeId(), Strings.EMPTY, null, MEActionChangeStatus.MR_DELETED);

            // Delete MrShareInfoStatus
            MrShareInfoStatusDeletionDTO mrShareInfoStatusDeletionDTO = new MrShareInfoStatusDeletionDTO();
            mrShareInfoStatusDeletionDTO.setReason(MrShareInfoStatusDeletionReason.MR);
            mrShareInfoStatusDeletionDTO.setMrOfficeUserId(officeUser.getId());
            mrShareInfoStatusService.deleteMrShareInfoStatus(mrShareInfoStatusDeletionDTO);

            //  call presentation to cancel all presentation request and change topic of MR to DELETE
            grpcClientPresentationService.cancelPresentationRequestWhenDeleteMR(officeUser.getId(), officeUser.getOfficeId(), loginInfo);
        }
        calendarService.removeDataWhenDelete(officeUser.getUserId(), officeUser.getOfficeId());

        // Update fire-base user's info: DR & MR
        grpcClientSideMenuService.updateUserInfo(commonService.createUpdateUserInfoRequest(Collections.singletonList(officeUser)));
        // Remove user in group and Update last article info.
        // Update presentation request
        grpcClientGroupService.removeGroupMember(officeUser);

        List<String> listOfficeUserIdOfDoctors = getDoctorsOfMediator(officeUser.getOfficeId(), officeUser);
        if (!CollectionUtils.isEmpty(listOfficeUserIdOfDoctors)) {
            grpcClientRtmService.update(commonService.createRTUser(officeUser, false, false, true, listOfficeUserIdOfDoctors));
        } else {
            grpcClientRtmService.update(commonService.createRTUser(officeUser, false, false, false, null));
        }

        // Delete User profile in elastic search
        grpcClientElasticService.deleteUserProfile(officeUser.getId());
        /*//delete handling hospital of this mr user
        if (officeUser instanceof MRUser) {

            //------------------------------------------------
            // purpose of this to reuse function "deleteHandlingHospitals" with any mr user
            UserAuthorityInfo info = new UserAuthorityInfo();
            info.setUserId(officeUser.getUserId());
            info.setOfficeId(officeUser.getOfficeId());
            LoginInfo mrDeleted = new LoginInfo(info);
            //------------------------------------------------

            MRUser mrUser = MRUser.class.cast(officeUser);
            if (!CollectionUtils.isEmpty(mrUser.getHandleOffices())) {
                mrUser.getHandleOffices().forEach(item -> {
                    REDeleteHandlingHospitalsRequest handlingHospitalsRequest = REDeleteHandlingHospitalsRequest.newBuilder()
                            .setOfficeId(item.getOfficeId()).build();
                    handlingHospitalService.deleteHandlingHospitals(handlingHospitalsRequest, mrDeleted);
                });
            }
        }*/

        // Delete meeting documents
        grpcClientWebMeetingService.deleteMeetingDocuments(officeUser);

        // update accountStatus in attendance-sync
        grpcClientAttendanceSyncService.updateAccountStatus(officeUser.getOfficeId(),officeUser.getId(),officeUser.getAccountStatuses());
    }

    /**
     * Get list REUserItem from list User
     *
     * @param usersRequest User search request
     * @param loginInfo User login info
     * @return List REListUsersResponse
     */
    public REListUsersResponse listUser(REListUsersRequest usersRequest, LoginInfo loginInfo) {
        List<OfficeUser> officeUsers;
        List<OfficeUser> phUsers;

        List<REUserItem> reUserItemList = new ArrayList<>();

        List<String> listUserDeny = new ArrayList<>();
        if (SHARE_STATUS_DENY.equals(usersRequest.getAdditionCondition())) {
            listUserDeny.add(loginInfo.getUserId());
            listUserDeny.addAll(
                    calendarService.getListUserWithShareStatusDeny(loginInfo.getOfficeId()));
        }
        UserSearchCondition conditions = new UserSearchCondition(
                usersRequest,
                loginInfo.getOfficeId(),
                listUserDeny
        );

        //get staff Medical
        if (loginInfo.getOfficeType() == REOfficeType.MEDICAL){
            officeUsers = officeUserRepository.findOfficeUserWithKeyword(conditions);
            reUserItemList = convertToREUserItems(officeUsers, loginInfo);
        }

        //get staff DrugStore
        if (loginInfo.getOfficeType() == REOfficeType.DRUG_STORE){
            phUsers = officeUserRepository.findPHUserWithKeyword(conditions);
            reUserItemList = convertToREUserItems(phUsers, loginInfo);
        }

        LOGGER.debug("End Method");
        return REListUsersResponse.newBuilder()
                .addAllUser(reUserItemList)
                .setPage(usersRequest.getPage().getPage())
                .build();
    }

    /**
     * get user info by email request
     *
     * @param request include email for search  and office id of current user is logging on system
     * @return user info
     */
    public REGetUserInfoByEmailResponse getUserInfoByEmail(REGetUserInfoByEmailRequest request) {
        LOGGER.debug("Start method - getUserInfoByEmail!");
        REGetUserInfoByEmailResponse.Builder builder = REGetUserInfoByEmailResponse.newBuilder();

        List<OfficeUser> officeUserList =
                officeUserRepository.findOfficeUsersByMailAddressOrLoginId(request.getEmail(),
                        request.getOfficeId());
        if (!officeUserList.isEmpty()) {
            officeUserList.forEach(officeUser -> {
                Office office = officeRepository.findOne(officeUser.getOfficeId());
                if (office != null) {
                    builder.addUser(getOfficeUserFromModel(officeUser, office.getName()));
                }
            });
        }

        LOGGER.debug("End Method");
        return builder.build();
    }

    /**
     * get Staff Inside
     *
     * @return REGetStaffInsideResponse
     */
    public REGetStaffInsideResponse getStaffInside(LoginInfo loginInfo) {
        LOGGER.debug("Start method - getStaffInside!");
        User user = userRepository.findOne(loginInfo.getUserId());
        if (user == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_USER)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }
        // Get officeUser by officeId
        OfficeUser officeUser = user.getOfficeUsers()
                .stream()
                .filter(oUser -> loginInfo.getOfficeId().equals(oUser.getOfficeId()))
                .findFirst()
                .orElse(null);

        // Throw if not found office
        if (officeUser == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_OFFICE)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        REGetStaffInsideResponse.Builder response = REGetStaffInsideResponse.newBuilder();

        // get list department
        REGetDepartmentResponse departments = departmentService.getDepartments(loginInfo);
        response.addAllDepartments(departments.getDepartmentList());

        List<OfficeUser> officeUsers = officeUserRepository
                .findByOfficeId(loginInfo.getOfficeId());

        if (!officeUsers.isEmpty()) {
            List<REPrepareDepartmentUser> rePrepareDepartmentUserList =
                    convertToREPrepareDepartmentUser(officeUsers);
            response.addAllUsers(rePrepareDepartmentUserList);
        }

        LOGGER.debug("End Method");
        return response.build();
    }

    /**
     * @param loginInfo of current user is create group
     * @return REGetPrepareEditInsideGroupResponse
     */
    public REGetPrepareEditInsideGroupResponse getPrepareEditInsideGroupResponse(
            LoginInfo loginInfo) {
        LOGGER.debug("Start method - getPrepareEditInsideGroupResponse");

        REGetPrepareEditInsideGroupResponse.Builder response =
                REGetPrepareEditInsideGroupResponse.newBuilder();

        // get list department
        REGetDepartmentResponse departments = departmentService.getDepartments(loginInfo);
        response.addAllDepartments(departments.getDepartmentList());

        List<OfficeUser> officeUsers =
                officeUserRepository.findListUserValid(loginInfo.getOfficeId(),OfficeUser.class.getName());
        if (!officeUsers.isEmpty()) {
            List<REPrepareDepartmentUser> rePrepareDepartmentUserList =
                    convertToREPrepareDepartmentUser(officeUsers);
            response.addAllUsers(rePrepareDepartmentUserList);
        }

        LOGGER.debug("End Method");
        return response.build();
    }

    /**
     * add user connection
     *
     * @param request REAddListUserConnectionRequest
     */
    public void addUserConnection(REAddListUserConnectionRequest request) {
        LOGGER.debug("Start method - addUserConnection!");
        List<REAddUserConnection> reAddUserConnectionList = request.getConnectionsList();
        if (!reAddUserConnectionList.isEmpty()) {
            List<UserConnection> listAddNew = new ArrayList<>();
            for (REAddUserConnection reAddUserConnection : reAddUserConnectionList) {
                List<UserConnection> listCheckExist
                        = userConnectionRepository.findListUserConnection(
                        reAddUserConnection.getOfficeUserId(),
                        reAddUserConnection.getConnectedOfficeUserId());
                if (listCheckExist.isEmpty()) {
                    // TODO: check logic again
                    OfficeUser userConnect =
                            officeUserRepository.findOne(reAddUserConnection.getOfficeUserId());
                    OfficeUser userConnected = officeUserRepository.findOne(
                            reAddUserConnection.getConnectedOfficeUserId());
                    if (userConnect == null || userConnected ==null){
                        throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_USER)
                                .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
                    }
                    UserConnection userConnection = new UserConnection();
                    userConnection.setConnectedOfficeUserId(
                            reAddUserConnection.getConnectedOfficeUserId());
                    userConnection.setConnectedOfficeId(userConnected.getOfficeId());

                    userConnection.setOfficeUserId(reAddUserConnection.getOfficeUserId());
                    userConnection.setOfficeId(userConnect.getOfficeId());

                    listAddNew.add(userConnection);
                }
            }

            if (!listAddNew.isEmpty()) {
                userConnectionRepository.saveAll(listAddNew);
            }
        }
        LOGGER.debug("End Method");
    }

    /**
     * Convert OfficeUser to REUserInfoFromEmail
     *
     * @param officeUser Office user model
     * @param officeName Office Name input
     * @return REUserInfoFromEmail
     */
    public REUserInfoFromEmail getOfficeUserFromModel(OfficeUser officeUser, String officeName) {

        return REUserInfoFromEmail.newBuilder()
                .setOfficeUserId(officeUser.getId())
                .setFirstName(Strings.nvl(officeUser.getFirstName()))
                .setFirstNameKana(Strings.nvl(officeUser.getFirstNameKana()))
                .setLastName(Strings.nvl(officeUser.getLastName()))
                .setLastNameKana(Strings.nvl(officeUser.getLastNameKana()))
                .setOfficeId(Strings.nvl(officeUser.getOfficeId()))
                .setOfficeName(officeName)
                .setImage(officeUser.getProfile() != null ? Strings.nvl(
                        officeUser.getProfile().getImage()) : "")
                .build();
    }

    /**
     * Convert user to REUserItem and add into list
     *
     * @param officeUserList List office user
     * @return List REUserItem
     */
    public List<REUserItem> convertToREUserItems(List<? extends OfficeUser> officeUserList,
            LoginInfo loginInfo) {
        List<REUserItem> reUserItemList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(officeUserList)) {
            for (OfficeUser officeUser : officeUserList) {
                REUserItem.Builder builder = REUserItem.newBuilder();
                if (StringUtils.isNotBlank(officeUser.getDepartmentId())) {

                    Department department =
                            departmentRepository.findOne(officeUser.getDepartmentId());

                    if (department != null) {
                        REDepartment departmentGRPCBuilder = REDepartment.newBuilder()
                                .setId(Strings.nvl(department.getId()))
                                .setDisplayName(Strings.nvl(department.getDisplayName()))
                                .setName(Strings.nvl(department.getName()))
                                .setPath(Strings.nvl(department.getPath()))
                                .build();
                        builder.setDepartment(departmentGRPCBuilder);
                    }
                }

                // get job name
                String jobName = null;
                if (officeUser.getJobType() != null) {
                    jobName = grpcClientMasterService.getJobName(officeUser.getJobType());
                }
                // Add basic info
                builder.setFirstName(Strings.nvl(officeUser.getFirstName()))
                        .setLoginId(Strings.nvl(officeUser.getLoginId()))
                        .setFirstNameKana(Strings.nvl(officeUser.getFirstNameKana()))
                        .setLastName(Strings.nvl(officeUser.getLastName()))
                        .setLastNameKana(Strings.nvl(officeUser.getLastNameKana()))
                        .setJobType(Strings.nvl(officeUser.getJobType()))
                        .setJobName(Strings.nvl(jobName))
                        .setAccountStatuses(officeUser.getAccountStatuses())
                        .setUserId(Strings.nvl(officeUser.getUserId()))
                        .setMailAddress(Strings.nvl(officeUser.getMailAddress()))
                        .setOfficeId(Strings.nvl(officeUser.getOfficeId()))
                        .setOfficeUserId(officeUser.getId());
                if (officeUser.getProfile() != null) {
                    builder.setImage(Strings.nvl(officeUser.getProfile().getImage()));
                }

                ManagementAuthority managementAuthority = officeUser.getManagementAuthority();
                if (managementAuthority != null) {
                    builder.setManagementAuthority(managementAuthority.asAuthorities());
                }

                ManagementLevel managementLevel = officeUser.getFp3ManagementLevel();
                if (managementLevel != null) {
                    builder.setFp3ManagementLevel(
                            REManagementLevel.forNumber(managementLevel.ordinal()));
                }
                ManagementLevel fp12ManagementLevel = officeUser.getFp12ManagementLevel();
                if (fp12ManagementLevel != null) {
                    builder.setFp12ManagementLevel(
                            REManagementLevel.forNumber(fp12ManagementLevel.ordinal()));
                }
                FuncAuthoritySet funcAuthoritySet = officeUser.getFuncAuthority();
                if (funcAuthoritySet != null) {
                    builder.setFuncAuthoritySet(funcAuthoritySet.asAuthority());
                    // custom authorities
                    if (funcAuthoritySet.asAuthority() == REFuncAuthoritySet.FPS_0) {
                        FuncAuthoritySet authoritySet = getFuncAuthoritySet(officeUser);
                        if (authoritySet != null) {
                            builder.setFuncAuthority(authoritySet.asAuthorities());
                        }
                    }
                }
                if (officeUser.getContact() != null) {
                    builder.setPhsNo(Strings.nvl(officeUser.getContact().getPhsNo()));
                    if (loginInfo != null && officeUser.getId()
                            .equals(loginInfo.getOfficeUserId())) {
                        builder.setMobileNo(Strings.nvl(officeUser.getContact().getMobileNo()));
                    } else {

                        PublishingType mailPublishingType =
                                officeUser.getContact().getMailAddressPublishingType();
                        if (mailPublishingType != null && mailPublishingType.name()
                                .equals(PublishingType.PRIVATE.name())) {
                            builder.setMailAddress("");
                        } else {
                            builder.setMailAddress(Strings.nvl(officeUser.getMailAddress()));
                        }
                        //#3313
                        PublishingType mobilePublishingType =
                                officeUser.getContact().getMobileNoPublishingType();
                        if (mobilePublishingType != null && mobilePublishingType.name()
                                .equals(PublishingType.PRIVATE.name())) {
                            builder.setMobileNo("");
                        } else {
                            builder.setMobileNo(Strings.nvl(officeUser.getContact().getMobileNo()));
                        }
                    }
                }
                reUserItemList.add(builder.build());
            }
        }
        return reUserItemList;
    }

    /**
     * <p> Get Authorities beyond on FuncAuthoritySet <br/> Note: Only apply for FunctionAuthoritySet is FPS_0 (custom)</p>
     *
     * @param officeUser {@link OfficeUser}
     * @return {@link FuncAuthoritySet}
     */
    public FuncAuthoritySet getFuncAuthoritySet(OfficeUser officeUser) {
        if (officeUser == null) {
            return null;
        }
        OfficeUser offUser = officeUserRepository.findOne(officeUser.getId());
        return offUser == null ? null : offUser.getFuncAuthority();
    }

    /**
     * <p> Lock user </p>
     *
     * @param userId UserId will be lock/unlock
     * @param officeId Current login officeId
     * @param lockFlg LockFlg: true - lock; false - unlock
     * @param loginInfo Current login info
     */
    public void lockOrUnlockUser(String userId, String officeId, boolean lockFlg,
            LoginInfo loginInfo) {
        // Check user exist
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_USER)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        if (user.getOfficeUsers().isEmpty()) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_OFFICE)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        // check officeId exist
        OfficeUser officeUser = user.getOfficeUsers().stream()
                .filter(offUser -> officeId.equals(offUser.getOfficeId()))
                .findFirst().orElse(null);

        if (officeUser == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_OFFICE)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }
        // lock account user
        lockOrUnlockAccountUser(lockFlg, officeUser, loginInfo);
    }

    /**
     * Lock or unlock MR user
     *
     * @param mrOfficeUserId
     * @param loginOfficeUserId
     * @param loginUserId
     * @param loginOfficeId
     * @param lockFlg
     */
    public void lockOrUnlockMRUser(String mrOfficeUserId,
                                   String loginOfficeUserId,
                                   String loginUserId,
                                   String loginOfficeId,
                                   boolean lockFlg) {
        LOGGER.info("MR {} is locked by loginOfficeUserId : {}, loginUserId : {}, loginOfficeUserId : {}"
                , mrOfficeUserId, loginOfficeUserId, loginUserId, loginOfficeId);

        //  TODO: waiting for spec (check if logging in user have the right to lock or not
        // Check officeUser exist
        OfficeUser officeUser = officeUserRepository.findOne(mrOfficeUserId);
        if (officeUser == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_OFFICE)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        AccountStatuses currentStatus = AccountStatuses.fromBits(officeUser.getAccountStatuses());
        AccountStatuses newStatus = currentStatus.setBit(AccountStatus.LOCKING, lockFlg);
        officeUser.setAccountStatuses(newStatus.getBits());
        // update into db
        officeUserRepository.save(officeUser);

        // Update fire-base user's info: MR
        grpcClientSideMenuService.updateUserInfo(commonService.createUpdateUserInfoRequest(Collections.singletonList(officeUser)));

        // TODO: waiting for spec (Call to other service for update if neccesary)
    }

    /**
     * Get user provisional
     *
     * @param request Get user request
     * @return GRPC User info
     * author TuanPD
     */
    public REListProvisionalUsersResponse getUserProvisional(REListProvisionalUsersRequest request,
            LoginInfo loginInfo) {
        UserProvisionalCondition condition = new UserProvisionalCondition(request);
        List<OfficeUser> officeUsers =
                officeUserRepository.findOfficeUsersProvisional(condition, loginInfo.getOfficeId());
        REListProvisionalUsersResponse.Builder builder =
                REListProvisionalUsersResponse.newBuilder();

        //if page = 0 and size = 0, export all user
        if(!(request.getPage().getPage()==0 && request.getPage().getSize()== 0)) {
            builder.setPage(request.getPage().getPage());
        }

        if (officeUsers != null && !officeUsers.isEmpty()) {
            builder.addAllStaffList(asREStaffs(officeUsers));
        }
        return builder.build();
    }

    /**
     * download user provisional
     * @param request
     * @param loginInfo
     * @return
     */
    public REDownloadProvisionalUsersResponse downloadProvisionalUsers(REListProvisionalUsersRequest request,
                                                                      LoginInfo loginInfo) {
        UserProvisionalCondition condition = new UserProvisionalCondition(request);
        List<OfficeUser> listOfficeUsers =
                officeUserRepository.findOfficeUsersProvisional(condition, loginInfo.getOfficeId());

        REProvisionalUsersFireBaseRequest.Builder reProvisionalUsersFireBaseRequest = REProvisionalUsersFireBaseRequest.newBuilder();
        reProvisionalUsersFireBaseRequest.setOfficeUSerId(Strings.nvl(loginInfo.getOfficeUserId()));
        if (listOfficeUsers != null && !listOfficeUsers.isEmpty()) {
            reProvisionalUsersFireBaseRequest.addAllStaffList(asREStaffs(listOfficeUsers));
        }
        REProvisionalUsersFireBaseResponse response = grpcClientSideMenuService.uploadProvisionalUsersCSV(reProvisionalUsersFireBaseRequest.build());

        REDownloadProvisionalUsersResponse.Builder builder =
                REDownloadProvisionalUsersResponse.newBuilder();
        builder.setPathDownload(response.getPathDownload());
        return builder.build();
    }

    /**
     * get list department
     *
     * @param loginInfo LoginInfo
     * @return REDepartment List
     * author LuongHH
     */
    public List<REDepartment> getDepartments(LoginInfo loginInfo) {
        User user = userRepository.findOne(loginInfo.getUserId());
        if (user == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_USER)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }
        // Get officeUser by officeId
        OfficeUser officeUser = user.getOfficeUsers()
                .
                        stream()
                .
                        filter(oUser -> loginInfo.getOfficeId().equals(oUser.getOfficeId()))
                .findFirst()
                .orElse(null);

        // Throw if not found office
        if (officeUser == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_OFFICE)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        List<REDepartment> reDepartmentList;
        // check authority
        if (officeUser.getManagementAuthority().getCode() == ManagementAuthority.MP_1.getCode()) {
            REGetDepartmentResponse reGetDepartmentResponse =
                    departmentService.getDepartments(loginInfo);
            reDepartmentList = reGetDepartmentResponse.getDepartmentList();
        } else if (officeUser.getManagementAuthority().getCode()
                == ManagementAuthority.MP_2.getCode()) {
            // Get department of office if user role is department admin
            Department department = departmentRepository.findOne(officeUser.getDepartmentId());
            if (department == null) {
                throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_DEPARTMENT)
                        .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
            }

            reDepartmentList = new ArrayList<>();
            reDepartmentList.add(convertDepartment2Re(department));
        } else {
            throw ServiceStatus.PERMISSION_DENIED.withMessage(NOT_PERMISSION)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        return reDepartmentList;
    }

    /**
     * Get list UserConnect info by list UserId
     *
     * @param request Input request
     * @return REListUserConnectionInfoResponse
     */
    public REGetUserByListResponse getUserByList(REGetUserByListRequest request) {
        List<OfficeUser> listOfficeUser = officeUserRepository.findAllByIdIn(request.getOfficeUserIdList());

        return convertListOfficeUserToResponse(listOfficeUser);
    }

    /**
     * Get list UserConnect info by officeUserIds or userIds
     * @param request
     * @return
     */
    public REGetUserByListResponse getUserInfoListByOfficeUserIdsOrUserIds(REGetUserByOfficeUserIdsOrUserIdsRequest request) {
        List<OfficeUser> listOfficeUser = officeUserRepository.findAllByIdInOrUserIdIn(request.getOfficeUserIdList(), request.getUserIdList());

        return convertListOfficeUserToResponse(listOfficeUser);
    }

    private REGetUserByListResponse convertListOfficeUserToResponse(List<OfficeUser> listOfficeUser){
        REGetUserByListResponse.Builder response = REGetUserByListResponse.newBuilder();
        Map<String, Office> officeMap = new HashMap<>();
        Map<String, Department> departmentMap = new HashMap<>();
        Map<String, String> jobMap = new HashMap<>();
        Map<String, String> jobMrMap = new HashMap<>();
        Map<String, String> nameAreaMap = new HashMap<>();
        Map<String, String> nameTypeMap = new HashMap<>();
        Map<String, String> prefectureNameMap = new HashMap<>();

        for (OfficeUser officeUser : listOfficeUser) {

            Office office = null;
            if (officeMap.containsKey(officeUser.getOfficeId())) {
                office = officeMap.get(officeUser.getOfficeId());
            } else {
                office = officeRepository.findOfficeBy(officeUser.getOfficeId());
                officeMap.put(officeUser.getOfficeId(), office);
            }

            Department department = null;
            if (departmentMap.containsKey(officeUser.getDepartmentId())) {
                department = departmentMap.get(officeUser.getDepartmentId());
            } else {
                department = departmentRepository.findOne(officeUser.getDepartmentId());
                if (office != null) {
                    departmentMap.put(officeUser.getDepartmentId(), department);
                }
            }

            String jobName = "";
            String prefectureName = "";
            if (officeUser instanceof MRUser) {
                MRUser mrUser = (MRUser) officeUser;
                Branch branch = mrUser.getBranch();
                if (jobMrMap.containsKey(mrUser.getJobType())) {
                    jobName = jobMrMap.get(mrUser.getJobType());
                } else {
                    MAListMrJobTypesResponse maListMrJobTypesResponse = grpcClientMasterService.listMrJobTypesByIds(Lists.newArrayList(mrUser.getJobType()));
                    MAMrJobType maMrJobType = maListMrJobTypesResponse.getJobTypesList().stream()
                            .filter(jobType -> jobType.getId().equals(mrUser.getJobType()))
                            .findFirst()
                            .orElse(null);
                    if (maMrJobType != null) {
                        jobName = maMrJobType.getJobName();
                        jobMrMap.put(mrUser.getJobType(), jobName);
                    }
                }

                if (branch != null) {
                    if (prefectureNameMap.containsKey(branch.getPrefectureCode())) {
                        prefectureName = prefectureNameMap.get(branch.getPrefectureCode());
                    } else {
                        MAPrefecture maPrefecture = grpcClientMasterService.getPrefecture(branch.getPrefectureCode());
                        prefectureName = maPrefecture.getName();
                        prefectureNameMap.put(branch.getPrefectureCode(), prefectureName);
                    }
                }

            } else {
                if (jobMap.containsKey(officeUser.getJobType())) {
                    jobName = jobMap.get(officeUser.getJobType());
                } else if (!Strings.isEmpty(officeUser.getJobType())) {
                    jobName = grpcClientMasterService.getJobName(officeUser.getJobType());
                    if (office != null) {
                        jobMap.put(officeUser.getJobType(), jobName);
                    }
                }
            }

            List<SpecializedDepartment> specializedDepartmentList = officeUser.getSpecializedDepartments();
            specializedDepartmentList.forEach(specializedDepartment -> {
                String nameArea = "";
                if (!nameAreaMap.containsKey(specializedDepartment.getFieldId())) {
                    nameArea = grpcClientMasterService.getNameAreaById(specializedDepartment.getFieldId());
                    nameAreaMap.put(specializedDepartment.getFieldId(), nameArea);
                }

                String nameType = "";
                if (!nameTypeMap.containsKey(specializedDepartment.getTypeId())) {
                    nameType = grpcClientMasterService.getNameTypeById(specializedDepartment.getTypeId());
                    nameTypeMap.put(specializedDepartment.getTypeId(), nameType);
                }
            });

            response.addUserByListInfo(REUserByListInfo.newBuilder()
                    .setOfficeUserId(officeUser.getId())
                    .setUserId(officeUser.getUserId())
                    .setOfficeId(Strings.nvl(officeUser.getOfficeId()))
                    .setOfficeName(office != null ? office.getName() : "")
                    .setInfo(getUserForListUser(officeUser, office, department, jobName, prefectureName, nameAreaMap, nameTypeMap))
                    .build());
        }

        return response.build();
    }

    public REOfficeUser getOfficeUser(REGetOfficeUserRequest request) {
        REOfficeUser ret;
        switch (request.getRequestByCase()) {
            case LOGIN:
                return getOfficeUser(request.getLogin().getLoginId(), request.getLogin().getPassword());
            case OFFICEUSERID:
                return getOfficeUser(request.getOfficeUserId());
            case USERID:
                return getOfficeUserByUserId(request.getUserId());
            default:
                throw ServiceStatus.INVALID_ARGUMENT.asStatusRuntimeException();
        }
    }

    public REOfficeUser getOfficeUser(String loginId, String password) {
        User user = userRepository.findFirstByLoginId(loginId);
        if (user == null || !encryptProxy.matches(password, user.getPassword())) {
            return null;
        }
        // 現状、ログインユーザーと事業所ユーザーは1:1関係なので最初の項目を決めで取る
        // FIXME: 1:*に対応する際に修正が必要
        OfficeUser officeUser = user.getOfficeUsers().get(0);
        return convertReOfficeUser(officeUser);
    }

    public REOfficeUser getOfficeUser(String officeUserId) {
        OfficeUser officeUser = officeUserRepository.findOne(officeUserId);
        return convertReOfficeUser(officeUser);
    }

    public REOfficeUser getOfficeUserByUserId(String userId) {
        OfficeUser officeUser = officeUserRepository.findFirstByUserId(userId);
        return convertReOfficeUser(officeUser);
    }

    public REListOfficeUserResponse getListOfficeUserByListOfficeUserId(List<String> listOfficeUserId) {
        REListOfficeUserResponse.Builder response = REListOfficeUserResponse.newBuilder();
        List<OfficeUser> listOfficeUser = officeUserRepository.findByIds(listOfficeUserId);
        listOfficeUser.forEach(officeUser -> {
            response.addUser(convertReOfficeUser(officeUser));
        });

        return response.build();
    }

    public REListOfficeUserResponse getListOfficeUserByListUserId(List<String> listUserId) {
        REListOfficeUserResponse.Builder response = REListOfficeUserResponse.newBuilder();
        List<OfficeUser> listOfficeUser = officeUserRepository.findByListUserId(listUserId);

        listOfficeUser.forEach(officeUser -> {
            response.addUser(convertReOfficeUser(officeUser));
        });

        return response.build();
    }

    public REListUserByOfficesResponse listUserByOffices(List<String> officeIds) {
        List<REOfficeUser> users = officeUserRepository.findByOfficeIds(officeIds)
                .stream()
                .map(user -> {
                    Profile profile = user.getProfile();
                    String imageUrl = StringUtils.EMPTY;
                    if (!Objects.isNull(profile)) {
                        imageUrl = Strings.nvl(profile.getImage());
                    }
                    return REOfficeUser.newBuilder()
                            .setOfficeUserId(user.getId())
                            .setFirstName(user.getFirstName())
                            .setLastName(user.getLastName())
                            .setFirstNameKana(user.getFirstNameKana())
                            .setLastNameKana(user.getLastNameKana())
                            .setImageFileId(imageUrl)
                            .build();
                }).collect(Collectors.toList());

        return REListUserByOfficesResponse.newBuilder()
                .addAllUser(users)
                .build();
    }

    /**
     * AP5006
     *
     * @param request REGetMailAdditionalRequest
     * @return REGetMailAdditionalResponse
     */
    public REListMailUnConfirmAdditionalResponse getListMailUserSettings(
            REListMailUnConfirmAdditionalRequest request) {
        REListMailUnConfirmAdditionalResponse.Builder response =
                REListMailUnConfirmAdditionalResponse.newBuilder();
        List<MailChangeRequest> mailList =
                mailChangeRepository.findByUserIdAndStatus(request.getUserId(),
                        MailChangeRequest.MailChangeRequestStatus.ACCEPTED);
        List<REMailUnConfirmAdditionalItem> reList = new ArrayList<>();
        long now = new Date().getTime();
        for (MailChangeRequest changeMailRequest : mailList) {
            REMailUnConfirmAdditionalItem.Builder mailAdditional = REMailUnConfirmAdditionalItem.newBuilder();
            if (changeMailRequest.getExpires() != null) {
                if (now < changeMailRequest.getExpires().getTime()) { // mail UnConfirm and Unexpired
                    mailAdditional.setMailAddress(changeMailRequest.getNewEmail());
                    mailAdditional.setConfirmed(false);
                    reList.add(mailAdditional.build());
                }
            }
        }
        response.addAllMails(reList);
        return response.build();
    }

    /**
     * List office user by office id
     * @param request
     * @return
     */
    public List<REOfficeUser> listOfficeUserByOfficeId(REListOfficeUserRequest request) {
        String officeId = request.getOfficeId();
        return convertToReOfficeUsers(officeUserRepository.findByOfficeId(officeId));
    }

    /**
     * List office user by multi condition
     * @param request
     * @return
     */
    public List<REOfficeUser> listOfficeUserByConditions(REListOfficeUserRequest request) {
        CMNPage paging = request.getPage();
        int page = paging.getPage();
        int size = paging.getSize();
        String officeId = request.getOfficeId();
        UserSearchCondition.Status status =  new UserSearchCondition.Status(AccountStatusUtils.ANY, AccountStatusUtils.ANY,
                AccountStatusUtils.OFF, AccountStatusUtils.OFF);
        // add condition search for Reception App
        UserSearchCondition condition = new UserSearchCondition();
        condition.setSortName("_id");
        condition.setSortDirection(1);//asc
        condition.setOfficeId(officeId);
        condition.setStatuses(status);
        // paging for new version Reception App
        if (size > 0) {
            condition.setPage(page);
            condition.setPageSize(size);
        }
        return convertToReOfficeUsers(officeUserRepository.findWithCondition(condition));
    }

    /**
     * Convert office User Repository to ReOfficeUser
     * @param officeUsers
     * @return
     */
    private List<REOfficeUser> convertToReOfficeUsers(List<OfficeUser> officeUsers) {
        Map<String, Department> departmentMap = new HashMap<>();
        Map<String, Office> officeMap = new HashMap<>();

        return officeUsers.stream().map(user -> {
            REOfficeUser.Builder builder = REOfficeUser.newBuilder();

            // 事業所の取得
            Office office = null;
            if (!Strings.isEmpty(user.getOfficeId())) {
                office = officeMap.get(user.getOfficeId());
                if (office == null) {
                    office = officeRepository.findOfficeBy(user.getOfficeId());
                    officeMap.put(user.getOfficeId(), office);
                }
            }
            Optional.ofNullable(office)
                    .ifPresent(o -> builder.setOfficeName(Strings.nvl(o.getName())));

            // 所属の取得
            Department department = null;
            if (!Strings.isEmpty(user.getDepartmentId())) {
                department = departmentMap.get(user.getDepartmentId());
                if (department == null) {
                    department = departmentRepository.findOne(user.getDepartmentId());
                    departmentMap.put(user.getDepartmentId(), department);
                }
            }
            Optional.ofNullable(department)
                    .ifPresent(d -> builder.setDeptName(Strings.nvl(d.getDisplayName())));

            return builder
                    .setOfficeUserId(user.getId())
                    .setOfficeId(Strings.nvl(user.getOfficeId()))
                    .setDeptId(Strings.nvl(user.getDepartmentId()))
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setFirstNameKana(user.getFirstNameKana())
                    .setLastNameKana(user.getLastNameKana())
                    .setFuncAuthority(user.getFuncAuthority().asAuthorities())
                    .build();
        }).collect(Collectors.toList());
    }

    public GetUserIdFromListResponse getUserIdFromList(GetUserIdFromListRequest request) {
        ProtocolStringList officeUserIdsList = request.getOfficeUseridsList();
        if (officeUserIdsList.isEmpty()) {
            return GetUserIdFromListResponse.getDefaultInstance();
        } else {
            List<GetOfficeUser> officeUserList = new ArrayList<>();
            List<OfficeUser> allByIdIn = officeUserRepository.findAllByIdIn(officeUserIdsList);
            allByIdIn.forEach(officeUser -> officeUserList.add(GetOfficeUser.newBuilder()
                    .setOfficeId(officeUser.getOfficeId())
                    .setUserId(officeUser.getUserId())
                    .setOfficeUserId(officeUser.getId()).build()));
            return GetUserIdFromListResponse.newBuilder()
                    .addAllOfficeUser(officeUserList)
                    .build();
        }
    }

    public void updateImageProfile(REUpdateImageProfileRequest request) {

        OfficeUser officeUser = officeUserRepository.findOne(request.getOfficeUserId());

        if (officeUser == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_USER)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        if (StringUtils.isNotBlank(request.getImageUrl())) {
            Profile profile = officeUser.getProfile();
            profile.setImage(request.getImageUrl());
            profileRepository.save(profile);
        }

        if (StringUtils.isNotBlank(request.getIdentificationImageUrl())) {
            // service call by ME0006 : update image for mr
            MRUser mrUser = MRUser.class.cast(officeUser);
            Identification identification = mrUser.getIdentification();
            if (identification == null) {
                identification = new Identification();
                identification.setImageUrl(request.getIdentificationImageUrl());
                identificationRepository.save(identification);
                mrUser.setIdentification(identification);
                officeUserRepository.save(mrUser);
            } else {
                identification.setImageUrl(request.getIdentificationImageUrl());
                identification.setFileName(request.getFileName());
                identification.setUpdated(Dates.now());
                identificationRepository.save(identification);
            }
        }
    }

    public void updateImageProfileOnly(REUpdateImageProfileOnlyRequest request) {

        OfficeUser officeUser = officeUserRepository.findOne(request.getOfficeUserId());

        if (officeUser == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_USER)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        Profile profile = officeUser.getProfile();
        profile.setImage(request.getImageUrl());
        profileRepository.save(profile);

        // Check not MRUser, update DRUser info firebase
        if (!(officeUser instanceof MRUser)) {
            //update userInfo to firebase
            grpcClientSideMenuService.updateUserInfo(commonService.createUpdateUserInfoRequest(Collections.singletonList(officeUser)));
        }

    }

    public REOfficeType getOfficeType(OfficeUser officeUser) {
        if (officeUser == null) {
            return null;
        }

        if (OfficeType.MEDICAL.equals(officeUser.getOfficeType())) {
            return REOfficeType.MEDICAL;
        }
        if (OfficeType.DRUG_STORE.equals(officeUser.getOfficeType())) {
            return REOfficeType.DRUG_STORE;
        }
        if (OfficeType.PHARMACY.equals(officeUser.getOfficeType())) {
            return REOfficeType.PHARMACY;
        } else {
            return REOfficeType.OTHER;
        }
    }

    /** ユーザパスワード変更 */
    public void putUserPassword(REPutUserPasswordRequest request) {

        // オフィスユーザ情報取得
        OfficeUser officeUser = officeUserRepository.findOne(request.getOfficeUserId());
        // データが存在しない場合はエラーとする
        if (officeUser == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_USER)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        // ユーザ情報取得
        User user = userRepository.findOne(officeUser.getUserId());
        // データが存在しない場合はエラーとする
        if (user == null) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_USER)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }

        // パスワード変更
        user.setPassword(encryptProxy.encrypt(request.getPassword()));
        userRepository.save(user);
    }

    /** 会社紐付け変更 */
    public void putPrAccountOffice(REPutUserOfficeRequest request) {

        // Prユーザ情報取得
        Optional<MRUser> mrUserOptional =
                officeUserRepository.findMRUserByOfficeUserId(request.getOfficeUserId());

        // データが存在しない場合はエラーとする
        if (!mrUserOptional.isPresent()) {
            throw ServiceStatus.NOT_FOUND.withMessage(NOT_FOUND_USER)
                    .addError(Message.COMMON_SAVE_FAILED).asStatusRuntimeException();
        }
        MRUser mrUser = mrUserOptional.get();

        // 担当病院のPrのofficeIdを変更後のofficeIdを変更する
        List<HandlingHospital> handleOffices = mrUser.getHandleOffices();
        if (!CollectionUtils.isEmpty(handleOffices)) {
            handleOffices.forEach(handlingHospital -> {
                handlingHospital.setMrOfficeId(request.getOfficeId());
            });
            handlingHospitalRepository.saveAll(handleOffices);
            mrUser.setHandleOffices(handleOffices);
        }

        // 会社紐付け変更
        mrUser.setOfficeId(request.getOfficeId());
        officeUserRepository.save(mrUser);
    }

    /** ユーザステータス変更 */
    public void putUserStatus(REPutUserStatusRequest request) {

        // オフィスユーザ情報取得
        OfficeUser officeUser = officeUserRepository.findOne(request.getOfficeUserId());

        // 変更後のアカウントステータスを取得
        AccountStatuses modifyAccountStatuses =
                AccountStatuses.fromBits(request.getAccountStatus());

        // アカウントステータスの状態を変更
        if (modifyAccountStatuses.isInvalid()) {
            User user = userRepository.findOne(officeUser.getUserId());
            if (request.getIsBitOn()) {
                // アカウント無効
                officeUser.invalidStatus();
                user.invalidStatus();
            } else {
                // ログインID・メールアドレス重複チェック
                checkDuplicate(officeUser);
                // アカウント無効解除(復活)
                officeUser.revivalStatus();
                user.revivalStatus();
            }
            // ユーザ情報更新
            userRepository.save(user);
        } else {
            // 現在のアカウントステータスを保持しつつ、新たにアカウントステータスを追加or削除する
            AccountStatus accountStatus;
            if (modifyAccountStatuses.isProvisional()) {
                accountStatus = AccountStatus.PROVISIONAL;
            } else if (modifyAccountStatuses.isValid()) {
                accountStatus = AccountStatus.VALID;
            } else if (modifyAccountStatuses.isLocking()) {
                accountStatus = AccountStatus.LOCKING;
            } else {
                // 想定外ステータスエラー
                throw ServiceStatus.NOT_FOUND.withMessage(
                        String.format("Not exist AccountStatus. statusBits:%s",
                                modifyAccountStatuses.getBits()))
                        .asStatusRuntimeException();
            }

            // アカウントステータス変更
            AccountStatuses accountStatuses =
                    AccountStatuses.fromBits(officeUser.getAccountStatuses());
            officeUser.setAccountStatuses(
                    accountStatuses.setBit(accountStatus, request.getIsBitOn()).getBits());
        }

        // オフィスユーザ情報更新
        officeUserRepository.save(officeUser);

        // chat情報更新
        grpcClientRtmService.update(commonService.createRTUser(officeUser, false, false, false, null));

        // firebase の userInfoを更新
        UpdateUserInfoRequest reqUserInfo = commonService.createUpdateUserInfoRequest(Collections.singletonList(officeUser));
        grpcClientSideMenuService.updateUserInfo(reqUserInfo);

        //update account status to attendance-sync service
        grpcClientAttendanceSyncService.updateAccountStatus(officeUser.getOfficeId(),officeUser.getId(),officeUser.getAccountStatuses());
    }

    public Map<String, REUser> getListUser(Map<String, String> userIdAndOfficeIds) {
        List<User> list = userRepository.findAllByIdIn(userIdAndOfficeIds.keySet());
        if (!CollectionUtils.isEmpty(list)) {
            Map<String, REUser> map = new HashMap<>();

            list.forEach(user -> {
                List<OfficeUser> officeUserList = user.getOfficeUsers();
                if (!CollectionUtils.isEmpty(officeUserList)) {
                    OfficeUser officeUser = officeUserList.stream()
                            .filter(offUser -> userIdAndOfficeIds.get(user.getId())
                                    .equals(offUser.getOfficeId())).findFirst().orElse(null);

                    if (officeUser != null) {
                        REUser.Builder reUser = REUser.newBuilder();

                        // REUser -> jobType
                        reUser.setJobType(Strings.nvl(officeUser.getJobType()));
                        if (officeUser.getJobType() != null && !officeUser.getJobType().isEmpty()) {
                            reUser.setJobName(Strings.nvl(
                                    grpcClientMasterService.getJobName(officeUser.getJobType())));
                        }

                        //set officeUserId
                        reUser.setOfficeUserId(Strings.nvl(officeUser.getId()));
                        // set firstName
                        reUser.setFirstName(Strings.nvl(officeUser.getFirstName()));
                        // set lastName
                        reUser.setLastName(Strings.nvl(officeUser.getLastName()));
                        // set accountStatuses with INVALID case
                        reUser.setAccountStatuses(officeUser.getAccountStatuses());

                        if (officeUser.getFuncAuthority() != null) {
                            REFuncAuthoritySet funcAuthoritySet = officeUser.getFuncAuthority().asAuthority();
                            REFuncAuthority funcAuthority = officeUser.getFuncAuthority().asAuthorities();
                            reUser.setFuncAuthoritySet(funcAuthoritySet);
                            reUser.setFuncAuthority(funcAuthority);
                        }

                        // set officeId
                        reUser.setOfficeId(Strings.nvl(officeUser.getOfficeId()));
                        // get office with officeId
                        Office office = officeRepository.findOne(officeUser.getOfficeId());

                        if (office != null) {
                            // set officeName
                            reUser.setOfficeName(office.getName());
                        }

                        Profile profile = officeUser.getProfile();
                        if (profile != null) {
                            // REUser -> image
                            reUser.setImageUrl(Strings.nvl(profile.getImage()));
                            // REUser -> urlImageProfile
                            reUser.setProfileImageUrl(Strings.nvl(profile.getImage()));
                        }

                        map.put(user.getId(), reUser.build());
                    }
                }
            });

            return map;
        } else {
            return null;
        }
    }

    /**
     *  ME0017
     */
    public REUserListRespone getListUserByOfficeIdAndUserId(Map<String, String> userIdAndOfficeIds) {
        REUserListRespone.Builder builder = REUserListRespone.newBuilder();

        List<User> list = userRepository.findAllByIdIn(userIdAndOfficeIds.keySet());
        if (!CollectionUtils.isEmpty(list)) {
            Map<String, Department> departmentMap = new HashMap<>();

            list.forEach(user -> {
                List<OfficeUser> officeUserList = user.getOfficeUsers();
                if (!CollectionUtils.isEmpty(officeUserList)) {
                    OfficeUser officeUser = officeUserList.stream()
                            .filter(offUser -> userIdAndOfficeIds.get(user.getId())
                                    .equals(offUser.getOfficeId())).findFirst().orElse(null);

                    if (officeUser != null) {
                        REUser.Builder reUser = REUser.newBuilder();

                        // REUser -> jobType
                        reUser.setJobType(Strings.nvl(officeUser.getJobType()));

                        //set officeUserId
                        reUser.setOfficeUserId(Strings.nvl(officeUser.getId()));
                        // set firstName
                        reUser.setFirstName(Strings.nvl(officeUser.getFirstName()));
                        // set lastName
                        reUser.setLastName(Strings.nvl(officeUser.getLastName()));
                        // set accountStatuses with INVALID case
                        reUser.setAccountStatuses(officeUser.getAccountStatuses());

                        if (StringUtils.isNotBlank(officeUser.getDepartmentId())) {
                            Department department;
                            if (!departmentMap.containsKey(officeUser.getDepartmentId())) {
                                department = departmentRepository.findOne(officeUser.getDepartmentId());
                                if (department != null) {
                                    departmentMap.put(officeUser.getDepartmentId(), department);
                                }
                            } else {
                                department = departmentMap.get(officeUser.getDepartmentId());
                            }

                            if (department != null) {
                                reUser.setDepartment(
                                        REDepartment.newBuilder()
                                                .setDisplayName(Strings.nvl(department.getDisplayName()))
                                                .setName(Strings.nvl(department.getName()))
                                                .setPath(Strings.nvl(department.getPath()))
                                                .build());
                            }
                        }

                        if (officeUser.getFuncAuthority() != null) {
                            REFuncAuthoritySet funcAuthoritySet = officeUser.getFuncAuthority().asAuthority();
                            REFuncAuthority funcAuthority = officeUser.getFuncAuthority().asAuthorities();
                            reUser.setFuncAuthoritySet(funcAuthoritySet);
                            reUser.setFuncAuthority(funcAuthority);
                        }

                        // set officeId
                        reUser.setOfficeId(Strings.nvl(officeUser.getOfficeId()));

                        Profile profile = officeUser.getProfile();
                        if (profile != null) {
                            // REUser -> image
                            reUser.setImageUrl(Strings.nvl(profile.getImage()));
                            // REUser -> urlImageProfile
                            reUser.setProfileImageUrl(Strings.nvl(profile.getImage()));
                        }

                        REUserResponse response = REUserResponse.newBuilder()
                                .setUserId(user.getId())
                                .setUser(reUser.build())
                                .build();
                        builder.addUserList(response);
                    }
                }
            });

            return builder.build();
        } else {
            return null;
        }
    }

    /** 　メールアドレスからユーザ情報取得 */
    public REGetUserByMailAddressResponse getUserByMailAddress(
            REGetUserByMailAddressRequest request) {

        // パラメータのメールアドレスがログインIDとメールアドレスに存在するユーザを取得
        User user = userRepository.findUserByMailAddressOrLoginId(request.getMailAddress(),
                request.getMailAddress());

        REGetUserByMailAddressResponse.Builder builder =
                REGetUserByMailAddressResponse.newBuilder();
        if (user != null) {
            REUser.Builder reUserBuilder = REUser.newBuilder();
            reUserBuilder.setLoginId(Strings.nvl(user.getLoginId()));
            reUserBuilder.setMailAddress(Strings.nvl(user.getMailAddress()));
            builder.setReUser(reUserBuilder);
        }
        return builder.build();
    }

    /**
     * get DrugStore User By Email
     * @param request {@link REGetDrugStoreByEmailRequest}
     * @return  {@link REGetDrugStoreByEmailResponse}
     * */
    public REGetDrugStoreByEmailResponse getDrugStoreUserByEmail(REGetDrugStoreByEmailRequest request) {
        REGetDrugStoreByEmailResponse.Builder builder = REGetDrugStoreByEmailResponse.newBuilder();

        List<OfficeUser> officeUserList = officeUserRepository.findDrugStoreUserByMailAddressOrLoginId(request.getEmail());
        if (!officeUserList.isEmpty()) {
            officeUserList.stream()
                    .filter(officeUser -> officeUser.getFuncAuthority().getAuthorities().contains(FuncAuthority.FP_8)
                            || officeUser.getFuncAuthority().getAuthorities().contains(FuncAuthority.FP_9))
                    .forEach(officeUser -> {
                        Office office = officeRepository.findOne(officeUser.getOfficeId());
                        if (office != null) {
                            builder.setDrugStoreUser(getOfficeUserFromModel(officeUser, office.getName()));
                        }
                    });
        }

        return builder.build();
    }

    /** Dr.JOY全体管理者ユーザ作成 */
    public RECreateDrAdminUserResponse createDrAdminUser(RECreateDrAdminUserRequest request) {

        // 事業所情報取得
        Optional<MedicalOffice> medicalOfficeOpt =
                officeRepository.findMedicalOffice(request.getOfficeId());
        if (!medicalOfficeOpt.isPresent()) {
            throw ServiceStatus.NOT_FOUND.withMessage(
                    String.format("Not exist MedicalOffice officeId:%s", request.getOfficeId()))
                    .asStatusRuntimeException();
        }
        MedicalOffice medicalOffice = medicalOfficeOpt.get();
        OfficeSettings officeSettings =
                Optional.ofNullable(officeSettingsRepository.findFirstByOfficeId(medicalOffice.getId()))
                        .orElse(new OfficeSettings());

        // ユーザ作成
        String temporaryPassword = RandomStringUtils.randomAlphanumeric(PASSWORD_MIN_LENGTH);
        String loginId = request.getMailAddress();
        if (StringUtils.isBlank(loginId)) {
            while (true) {
                loginId = RandomStringUtils.randomNumeric(LOGIN_ID_MIN_LENGTH);
                User user = userRepository.findFirstByLoginId(loginId);
                if (user == null) {
                    break;
                }
            }
        }
        User user = createUser(temporaryPassword, loginId, request.getMailAddress());
        userRepository.save(user);

        // 設定作成
        Settings settings = commonService.settingNotification(true);

        // 連絡先(ElasticSearchでコンタクト情報を取得しているので、空のコンタクト情報を登録する
        Contact contact = new Contact();
        contactRepository.save(contact);

        // オフィスユーザ作成
        OfficeUser officeUser = createOfficeUser(request, medicalOffice, user, settings, contact);
        officeUser = officeUserRepository.save(officeUser);

        // ユーザにオフィスユーザを設定
        user.setOfficeUsers(Lists.newArrayList(officeUser));
        userRepository.save(user);

        // ElasticSearchに登録
        grpcClientElasticService.updateUserProfile(officeUser, "");

        // Dr.JOY管理者ユーザへメール送信
        try {
            LOGGER.debug("Dr.JOY管理者ユーザへのメール送信通知処理実施");
            commonService.publish(ServiceType.AM, NotificationID.AM010, officeUser.getId());
        } catch (Exception e) {
            LOGGER.error(
                    String.format("Dr.JOY管理者ユーザへのメール送信に失敗しました。 officeUserId:%s", user.getId()));
        }

        // レスポンス設定
        RECreateDrAdminUserResponse reCreateDrAdminUserResponse =
                toRECreateDrAdminUserResponse(medicalOffice, user, officeUser, officeSettings);

        return reCreateDrAdminUserResponse;
    }

    /**
     * Update image profile and identification image
     * @param request
     */
    public void updateImageProfileAndIdentificationImage(UpdateImageProfileAndIdentificationImageRequest request){
        OfficeUser officeUser = officeUserRepository.findOne(request.getOfficeUserId());

        if (officeUser != null && officeUser instanceof MRUser) {
            MRUser mrUser = MRUser.class.cast(officeUser);

            Profile profile = mrUser.getProfile();
            profile.setImage(request.getImageUrlFireBase());
            profileRepository.save(profile);

            Identification identification = mrUser.getIdentification();
            identification.setImageUrl(request.getIdentificationImageUrlFireBase());
            identificationRepository.save(identification);
        }

    }

    // Private methods
    // ------------------------------------------------------------------------
    private void setProfileReUser(Profile profile, REUser.Builder reUser) {
        // set hobby
        reUser.setHobby(Strings.nvl(profile.getHobby()));

        // set place born in
        reUser.setPlaceBornIn(Strings.nvl(profile.getPlaceBornIn()));

        // set position
        reUser.setPosition(Strings.nvl(profile.getPosition()));

        // set qualification
        reUser.setQualification(Strings.nvl(profile.getQualification()));

        // set brief history
        if (profile.getBriefHistory() != null) {
            reUser.setBriefHistory(profile.getBriefHistory());
        }

        // REUser -> graduate date
        if (profile.getGraduationDate() != null) {
            reUser.setGraduationDate(Dates.formatUTC(profile.getGraduationDate()));
        } else {
            // Set default GraduationDate
            Date date = new Date();
            reUser.setGraduationDate(Dates.formatUTC(date));
        }

        // REUser -> image
        reUser.setImageUrl(Strings.nvl(profile.getImage()));

        // REUser -> urlImageProfile
        reUser.setProfileImageUrl(Strings.nvl(profile.getImage()));

        // REUser -> gender type
        if (profile.getGenderType() != null) {
            reUser.setGender(REGender.forNumber(profile.getGenderType().ordinal()));
        }
    }

    /**
     * Convert Auth data from model to GPRC
     *
     * @param officeUser Curent OfficeUser
     * @param reUser GRPC Oject holder data
     */
    private void setAuthorityReUser(OfficeUser officeUser, REUser.Builder reUser) {
        if (officeUser.getManagementAuthority() != null) {
            reUser.setManagementAuthority(officeUser.getManagementAuthority().asAuthority());
        }

        if (officeUser.getFuncAuthority() != null) {
            REFuncAuthoritySet funcAuthoritySet = officeUser.getFuncAuthority().asAuthority();
            REFuncAuthority funcAuthority = officeUser.getFuncAuthority().asAuthorities();
            reUser.setFuncAuthoritySet(funcAuthoritySet);
            reUser.setFuncAuthority(funcAuthority);
        }

        if (officeUser.getFp3ManagementLevel() != null) {
            reUser.setFp3ManagementLevel(
                    REManagementLevel.forNumber(officeUser.getFp3ManagementLevel().ordinal()));
        }
        if (officeUser.getFp12ManagementLevel() != null) {
            reUser.setFp12ManagementLevel(
                    REManagementLevel.forNumber(officeUser.getFp12ManagementLevel().ordinal()));
        }
        if (officeUser.getFp15ManagementLevel() != null) {
            reUser.setFp15ManagementLevel(
                    REManagementLevel.forNumber(officeUser.getFp15ManagementLevel().ordinal()));
        }
    }

    private void setContactReUser(Contact contact, REUser.Builder reUser) {

        // REUser -> mobile no publish type
        if (contact.getMobileNoPublishingType() != null) {
            reUser.setMobileNoPublishingType(
                    REPublishingType.forNumber(contact.getMobileNoPublishingType().ordinal()));
        }

        // REUser -> mail address  publish type
        if (contact.getMailAddressPublishingType() != null) {
            reUser.setMailAddressPublishingType(
                    REPublishingType.forNumber(contact.getMailAddressPublishingType().ordinal()));
        }

        // REUser -> phsNo
        reUser.setPhsNo(Strings.nvl(contact.getPhsNo()));
    }

    private void setContactUser(REUser request, OfficeUser officeUser) {
        Contact contact = officeUser.getContact() == null ? new Contact() : officeUser.getContact();
        boolean setContactData = false;

        if (!request.getMobileNo().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            contact.setMobileNo(request.getMobileNo());
            setContactData = true;
        }
        if (!request.getPhsNo().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            contact.setPhsNo(request.getPhsNo());
            setContactData = true;
        }

        if (!request.getMailAddress().equals(REQUEST_PARAM_DEFAULT_VALUE)) {
            contact.setMailAddress(request.getMailAddress());
            setContactData = true;
        }
        if (request.getMailAddressPublishingType().compareTo(REPublishingType.UNRECOGNIZED) != 0) {
            contact.setMailAddressPublishingType(
                    contact.asPublishType(request.getMailAddressPublishingType().getNumber()));
        }
        if (request.getMobileNoPublishingType().compareTo(REPublishingType.UNRECOGNIZED) != 0) {
            contact.setMobileNoPublishingType(
                    contact.asPublishType(request.getMobileNoPublishingType().getNumber()));
        }
        if (setContactData) {
            officeUser.setContact(contact);
        }
    }

    /**
     * @param officeUserList current list office user convert
     * @return List<REPrepareDepartmentUser>
     */
    private List<REPrepareDepartmentUser> convertToREPrepareDepartmentUser(
            List<OfficeUser> officeUserList) {
        List<REPrepareDepartmentUser> rePrepareDepartmentUserList = new ArrayList<>();
        officeUserList.forEach(officeUser -> {
            String imageUrl = officeUser.getProfile() != null ? Strings.nvl(officeUser.getProfile().getImage()) : "";

            rePrepareDepartmentUserList.add(
                    REPrepareDepartmentUser.newBuilder()
                            .setOfficeUserId(Strings.nvl(officeUser.getId()))
                            .setFirstName(Strings.nvl(officeUser.getFirstName()))
                            .setFirstNameKana(Strings.nvl(officeUser.getFirstNameKana()))
                            .setLastName(Strings.nvl(officeUser.getLastName()))
                            .setLastNameKana(Strings.nvl(officeUser.getLastNameKana()))
                            .setDeptId(Strings.nvl(officeUser.getDepartmentId()))
                            .setAccountStatuses(officeUser.getAccountStatuses())
                            .setOfficeId(Strings.nvl(officeUser.getOfficeId()))
                            .setImageUrl(imageUrl)
                            .setManagementAuthority(officeUser.getManagementAuthority().asAuthority())
                            .setFuncAuthoritySet(officeUser.getFuncAuthority().asAuthority())
                            .setFuncAuthority(officeUser.getFuncAuthority().asAuthorities())
                            .build())
            ;
        });


        return rePrepareDepartmentUserList.isEmpty() ? Collections.emptyList()
                : rePrepareDepartmentUserList;
    }

    /**
     * Save office user data
     *
     * @param lockFlg true - lock, false - unlock
     * @param officeUser {@link OfficeUser}
     */
    private void lockOrUnlockAccountUser(boolean lockFlg, OfficeUser officeUser,
            LoginInfo loginInfo) {

        // Check if current user if admin group or admin chat room
        if (lockFlg) {
            // Check admin group
            GRCheckMemberIsOnlyAdminResponse grCheckMemberIsOnlyAdminResponse = grpcClientGroupService.checkMemberIsAdmin(officeUser.getId());
            if (grCheckMemberIsOnlyAdminResponse.getIsOnlyAdmin() && grCheckMemberIsOnlyAdminResponse != null) {
                throw ServiceStatus.NOT_FOUND.withMessage(RE0016_E001)
                        .addError(RE0016_E001).asStatusRuntimeException();
            }
            // Check admin chat room
            RTCheckIsExclusivelyAdminResponse rtCheckIsExclusivelyAdminResponse = grpcClientRtmService.checkIsExclusivelyAdmin(officeUser.getId());
            if (rtCheckIsExclusivelyAdminResponse.getIsExclusivelyAdmin() && rtCheckIsExclusivelyAdminResponse != null) {
                throw ServiceStatus.NOT_FOUND.withMessage(RE0016_E002)
                        .addError(RE0016_E002).asStatusRuntimeException();
            }

        }

        AccountStatuses currentStatus = AccountStatuses.fromBits(officeUser.getAccountStatuses());
        AccountStatuses newStatus = currentStatus.setBit(AccountStatus.LOCKING, lockFlg);
        officeUser.setAccountStatuses(newStatus.getBits());
        // update into db
        officeUserRepository.save(officeUser);

        // update fire base user info
        Department department = departmentRepository.findOne(officeUser.getDepartmentId());

        commonService.updateFIRUserAndGroupInfo(departmentService,department, officeUser.getDepartmentId(), loginInfo.getUserId(),
                officeUser, false);
        grpcClientRtmService.updateUserUsingSync(commonService.createRTUser(officeUser, false, false, false, null));

        // Call meeting to change status of meeting request
        if (lockFlg) {
            if (OfficeType.MEDICAL.equals(officeUser.getOfficeType()) || OfficeType.DRUG_STORE.equals(officeUser.getOfficeType())) {
                grpcClientMeetingService
                        .changeStatusMeetingRequest(officeUser.getUserId(), officeUser.getOfficeId(),
                                Strings.EMPTY, Strings.EMPTY, Strings.EMPTY,null, MEActionChangeStatus.DR_LOCKED);

                // CR #11667: Delete MrShareInfoStatus
                MrShareInfoStatusDeletionDTO mrShareInfoStatusDeletionDTO = new MrShareInfoStatusDeletionDTO();
                mrShareInfoStatusDeletionDTO.setReason(MrShareInfoStatusDeletionReason.DR);
                mrShareInfoStatusDeletionDTO.setDrOfficeUserId(officeUser.getId());
                mrShareInfoStatusService.deleteMrShareInfoStatus(mrShareInfoStatusDeletionDTO);
            }
            //calendarService.removeDataWhenDelete(officeUser.getUserId(), officeUser.getOfficeId());
        }
        //update accountStatus in attendance-sync
        grpcClientAttendanceSyncService.updateAccountStatus(officeUser.getOfficeId(),officeUser.getId(),officeUser.getAccountStatuses());
    }

    /**
     * Convert OfficeUser to REStaff add to list
     *
     * @param officeUserList : List OfficeUser
     * @return REDepartment
     * author TuanPD
     */
    public List<REStaff> asREStaffs(List<OfficeUser> officeUserList) {
        List<REStaff> reStaffs = new ArrayList<>();
        for (OfficeUser officeUser : officeUserList) {
            User user = userRepository.findOne(officeUser.getUserId());
            Department department = departmentRepository.findOne(officeUser.getDepartmentId());
            REStaff.Builder builder = REStaff.newBuilder();
            builder.setLastName(officeUser.getLastName());
            builder.setFirstName(officeUser.getFirstName());
            builder.setLastNameKana(officeUser.getLastNameKana());
            builder.setFirstNameKana(officeUser.getFirstNameKana());

            if (officeUser.getJobType() != null) {
                builder.setJobType(officeUser.getJobType());
                builder.setJobName(grpcClientMasterService.getJobName(officeUser.getJobType()));
            }
            if (user.getLoginId() != null) {
                builder.setLoginId(user.getLoginId());
            }
            if (user.getTemporaryPassword() != null) {
                builder.setTemporaryPassword(user.getTemporaryPassword());
            }
            if (department != null) {
                builder.setDepartment(asREDepartment(department));
            }

            reStaffs.add(builder.build());
        }
        return reStaffs;
    }

    /**
     * Convert Department to REDepartment
     *
     * @param department Department model
     * @return REDepartment
     * author TuanPD
     */
    private REDepartment asREDepartment(Department department) {
        REDepartment.Builder builder = REDepartment.newBuilder();
        builder.setId(department.getId());
        builder.setName(department.getName());
        builder.setDisplayName(department.getDisplayName());
        builder.setPath(department.getPath());
        return builder.build();
    }

    /**
     * Convert Department List to ReDepartment List
     *
     * @param department {@link Department}
     * @return REDepartment
     * @author NghiemPV
     */
    private REDepartment convertDepartment2Re(Department department) {

        List<REDepartment> listReDepartment = new ArrayList<>();
        if (!department.getChildren().isEmpty() && department.getChildren().size() != 0) {
            for (Department child : department.getChildren()) {
                listReDepartment.add(convertDepartment2Re(child));
            }
        }

        String displayName = Strings.nvl(department.getDisplayName());
        String name = Strings.nvl(department.getName());

        return REDepartment.newBuilder()
                .setId(Strings.nvl(department.getId()))
                .setDisplayName(displayName.isEmpty() ? name : displayName)
                .setPath(Strings.nvl(department.getPath()))
                .setName(Strings.nvl(department.getName()))
                .addAllChildren(listReDepartment)
                .build();
    }

    private REOfficeUser convertReOfficeUser(OfficeUser user) {
        if (user == null) {
            return null;
        }

        REOfficeUser.Builder builder = REOfficeUser.newBuilder();

        // オフィス情報
        Office office = officeRepository.findOne(Strings.nvl(user.getOfficeId()));
        Optional.ofNullable(office).ifPresent(o -> builder.setOfficeName(Strings.nvl(o.getName())));
        // 所属情報
        Department department = departmentRepository.findOne(Strings.nvl(user.getDepartmentId()));
        Optional.ofNullable(department)
                .ifPresent(d -> builder.setDeptName(Strings.nvl(d.getDisplayName())));
        if (OfficeType.MEDICAL.equals(user.getOfficeType()) || OfficeType.DRUG_STORE.equals(user.getOfficeType())) {
            List<RESpecializedDepartment> reSpecializedDepartmentList = new ArrayList<>();
            List<SpecializedDepartment> specializedDepartmentList =
                    user.getSpecializedDepartments();
            specializedDepartmentList.forEach(specializedDepartment -> {
                RESpecializedDepartment.Builder reSpecializedDepartment =
                        RESpecializedDepartment.newBuilder();
                // set name field
                reSpecializedDepartment.setFieldId(Strings.nvl(specializedDepartment.getFieldId()));
                //set Specialized with type
                reSpecializedDepartment.setTypeId(Strings.nvl(specializedDepartment.getTypeId()));
                reSpecializedDepartmentList.add(reSpecializedDepartment.build());
            });
            builder.addAllSpecializedDepartment(reSpecializedDepartmentList);
            builder.setJobType(Strings.nvl(user.getJobType()));
        }

        // get office with officeId
        Office officeInfo = officeRepository.findOne(user.getOfficeId());
        if (officeInfo != null) {
            // set officeName
            builder.setOfficeName(Strings.nvl(officeInfo.getName()));
            // set Name Abbreviation
            builder.setNameAbbreviation(Strings.nvl(officeInfo.getNameAbbreviation()));
        }
        builder.setOfficeUserId(user.getId())
                .setUserId(user.getUserId())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setFirstNameKana(user.getFirstNameKana())
                .setLastNameKana(user.getLastNameKana())
                .setDeptId(Strings.nvl(user.getDepartmentId()))
                .setOfficeId(Strings.nvl(user.getOfficeId()))
                .setOfficeType(user.getOfficeType().toMessage())
                .setAccountStatus(user.getAccountStatuses())
                .setManagementAuthority(user.getManagementAuthority().asAuthorities())
                .setFuncAuthoritySet(user.getFuncAuthority().asAuthority())
                .setFuncAuthority(user.getFuncAuthority().asAuthorities())
                .setImageFileId(
                        user.getProfile() != null ? Strings.nvl(user.getProfile().getImage()) : "");

        return builder.build();
    }

    /** User情報を生成 */
    private User createUser(String temporaryPassword, String loginId, String mailAddress) {

        User user = new User();
        user.setLoginId(loginId);
        user.setMailAddress(mailAddress);
        user.setTemporaryPassword(temporaryPassword);
        user.setPassword(encryptProxy.encrypt(temporaryPassword));
        return user;
    }

    /** OfficeUser情報を生成 */
    private OfficeUser createOfficeUser(RECreateDrAdminUserRequest request,
            MedicalOffice medicalOffice, User user, Settings settings, Contact contact) {

        Department department = new Department();
        if (!CollectionUtils.isEmpty(medicalOffice.getDepartments())) {
            // "所属未分類"のデータが取得される想定
            department = medicalOffice.getDepartments().get(0);
        }

        OfficeUser officeUser = new OfficeUser();
        officeUser.setUserId(user.getId());
        officeUser.setLoginId(user.getLoginId());
        officeUser.setTemporaryPassword(user.getTemporaryPassword());
        officeUser.setOfficeId(medicalOffice.getId());
        officeUser.setOfficeType(medicalOffice.getOfficeType());
        officeUser.setDepartmentId(department.getId());
        officeUser.setPath(department.getPath());
        if (StringUtils.isNotBlank(request.getMailAddress())) {
            officeUser.setMailAddress(request.getMailAddress());
        }
        // アカウントステータス [仮登録]
        officeUser.setAccountStatuses(AccountStatuses.fromAccountStatus(
                jp.drjoy.service.framework.model.AccountStatus.PROVISIONAL).getBits());
        // 管理権限 [全体管理者]
        officeUser.setManagementAuthority(ManagementAuthority.MP_1);
        // 機能権限 [なし]
        officeUser.setFuncAuthority(FuncAuthoritySet.FPS_4);
        // 面会状況管理範囲 [院内全て]
        officeUser.setFp3ManagementLevel(ManagementLevel.All);
        officeUser.setLastName(DEFAULT_OFFICE_USER_LAST_NAME);
        officeUser.setLastNameKana(DEFAULT_OFFICE_USER_LAST_NAME_KANA);
        officeUser.setFirstName(DEFAULT_OFFICE_USER_FIRST_NAME);
        officeUser.setFirstNameKana(DEFAULT_OFFICE_USER_FIRST_NAME_KANA);
        officeUser.setJobType(DEFAULT_OFFICE_USER_JOB_TYPE);

        // 設定情報
        officeUser.setSettings(settings);

        // 連絡先
        officeUser.setContact(contact);

        return officeUser;
    }

    /** RECreateDrAdminUserResponseに変換 */
    private RECreateDrAdminUserResponse toRECreateDrAdminUserResponse(MedicalOffice medicalOffice,
            User user, OfficeUser officeUser, OfficeSettings officeSettings) {
        RECreateDrAdminUserResponse.Builder builder = RECreateDrAdminUserResponse.newBuilder();

        builder.setUserId(user.getId());
        builder.setOfficeId(medicalOffice.getId());
        builder.setOfficeUserId(officeUser.getId());
        builder.setLoginId(user.getLoginId());
        builder.setPassword(user.getTemporaryPassword());
        builder.setKeyCode(medicalOffice.getKeyCode());
        builder.setRestrictedKeyCode(medicalOffice.getRestrictedKeyCode());
        builder.setReManagementAuthority(officeUser.getManagementAuthority().asAuthority());
        builder.setReFuncAuthoritySet(officeUser.getFuncAuthority().asAuthority());
        builder.setReFuncAuthority(officeUser.getFuncAuthority().asAuthorities());
        builder.setSnsEnabled(officeSettings.isSnsEnabled());
        return builder.build();
    }

    public REUserListRespone getListHandleUsers(String userId, String officeId) {
        REUserListRespone.Builder response = REUserListRespone.newBuilder();
        MEListHandleUsersResponse handleUsers = grpcClientMeetingService.getListIdHandleUsers(userId, officeId);
        List<String> userIds = handleUsers.getUsersList().stream().map(MEUser::getUserId).collect(Collectors.toList());



        List<OfficeUser> list = officeUserRepository.findByUserIdsAndFuncAuthorityAndStatuses(
                userIds,
                FUNC_AUTHORITY_FP1,
                AccountStatusUtils.getValidProvisionalStatus());

        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(officeUser -> {
                REUserResponse.Builder user = REUserResponse.newBuilder();
                user.setUserId(officeUser.getUserId());
                Profile profile = officeUser.getProfile();
                user.setUser(REUser.newBuilder()
                        .setFirtNameKana(Strings.nvl(officeUser.getFirstNameKana()))
                        .setLastNameKana(Strings.nvl(officeUser.getLastNameKana()))
                        .setFirstName(Strings.nvl(officeUser.getFirstName()))
                        .setLastName(Strings.nvl(officeUser.getLastName()))
                        .setOfficeId(Strings.nvl(officeUser.getOfficeId()))
                        .setImageUrl(profile == null ? "" : Strings.nvl(profile.getImage()))
                        .setDepartment(REDepartment.newBuilder().setId(officeUser.getDepartmentId()).build())
                        .build());
                response.addUserList(user);
            });
        }
        return response.build();
    }

    public REGetUserByListResponse getListMemberInfo (List<String>  officeUserIds) {
        List<REUserByListInfo> reUserByListInfos = new ArrayList<>();
        Map<String, Department> departmentMap = new HashMap<>();
        List<OfficeUser> officeUserList = officeUserRepository.findAllByIdIn(officeUserIds);
        Map<String, Office> officeMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(officeUserList)){
            officeUserList.forEach(officeUser -> {
                Office office = null;
                if (officeMap.containsKey(officeUser.getOfficeId())) {
                    office = officeMap.get(officeUser.getOfficeId());
                } else if (!Strings.isEmpty(officeUser.getOfficeId())) {
                    office = officeRepository.findOfficeBy(officeUser.getOfficeId());
                    if (office != null) {
                        officeMap.put(officeUser.getOfficeId(), office);
                    }
                }
                String officeName = "";
                String officeId = "";
                REUser.Builder reUser = REUser.newBuilder();
                if (office != null) {
                    officeName = office.getName();
                    officeId = office.getId();
                }
                reUser.setFirstName(Strings.nvl(officeUser.getFirstName()));
                reUser.setLastName(Strings.nvl(officeUser.getLastName()));
                reUser.setFirtNameKana(Strings.nvl(officeUser.getFirstNameKana()));
                reUser.setLastNameKana(Strings.nvl(officeUser.getLastNameKana()));
                reUser.setAccountStatuses(officeUser.getAccountStatuses());
                reUser.setFuncAuthority(officeUser.getFuncAuthority().asAuthorities());
                reUser.setJobType(Strings.nvl(officeUser.getJobType()));
                reUser.setBirthDate(Strings.nvl(Dates.formatUTC(officeUser.getBirthDate())));
                if (officeUser.getProfile() != null) {
                    reUser.setImageUrl(Strings.nvl(officeUser.getProfile().getImage()));
                }
                if (!Strings.isEmpty(officeUser.getDepartmentId())) {
                    Department department;
                    if (!departmentMap.containsKey(officeUser.getDepartmentId())) {
                        department = departmentRepository.findOne(officeUser.getDepartmentId());
                        if (department != null) {
                            departmentMap.put(officeUser.getDepartmentId(), department);
                        }
                    } else {
                        department = departmentMap.get(officeUser.getDepartmentId());
                    }
                    if (department != null) {
                        String displayName = Strings.nvl(department.getDisplayName());
                        String name = Strings.nvl(department.getName());
                        reUser.setDepartment(REDepartment.newBuilder()
                                .setId(Strings.nvl(department.getId()))
                                .setDisplayName(displayName)
                                .setName(name).build());
                    }
                }
                if (officeUser.getFp12ManagementLevel() != null)
                    reUser.setFp12ManagementLevel(officeUser.getFp12ManagementLevel().asREManagementLevel());
                if (officeUser.getFp15ManagementLevel() != null)
                    reUser.setFp15ManagementLevel(officeUser.getFp15ManagementLevel().asREManagementLevel());
                reUserByListInfos.add(REUserByListInfo.newBuilder()
                        .setOfficeName(officeName)
                        .setOfficeId(officeId)
                        .setOfficeUserId(officeUser.getId())
                        .setUserId(officeUser.getUserId())
                        .setInfo(reUser).build()
                );
            });
        } else {
            return REGetUserByListResponse.getDefaultInstance();
        }
        return REGetUserByListResponse.newBuilder()
                .addAllUserByListInfo(reUserByListInfos)
                .build();
    }

    public REGetUserProvisionalFromListResponse getUserProvisionalByListOfficeUserId(List<String> officeUserIdList) {
        List<String> userProvisionalList = new ArrayList<>();
        int accountStatus = AccountStatuses.fromAccountStatus(AccountStatus.PROVISIONAL).getBits();
        List<OfficeUser> officeUserList = officeUserRepository.findByIdsAndAccountStatuses(officeUserIdList, accountStatus);
        if (!CollectionUtils.isEmpty(officeUserList)) {
            officeUserList.forEach(officeUser -> {
                userProvisionalList.add(officeUser.getId());
            });
        } else {
            return REGetUserProvisionalFromListResponse.getDefaultInstance();
        }
        return REGetUserProvisionalFromListResponse.newBuilder()
                .addAllOfficeUserIdProvisional(userProvisionalList)
                .build();
    }

    /**
     * Get DR info by officeUserId
     * @param request
     * @param loginInfo
     * @return
     */
    public REUser getUserByOfficeUserId(REGetUserByOfficeUserIdRequest request, LoginInfo loginInfo) {
        OfficeUser officeUser = officeUserRepository.findOne(request.getOfficeUserId());
        if (officeUser == null) {
            return REUser.getDefaultInstance();
        }

        return convertOfficeUserToReUser(officeUser, loginInfo);
    }

    /**
     * Get DR info by officeUserId or userId
     * @param request
     * @param loginInfo
     * @return
     */
    public REUser getUserByOfficeUserIdOrUserId(REGetUserByOfficeUserIdOrUserIdRequest request, LoginInfo loginInfo) {
        OfficeUser officeUser = officeUserRepository.findOne(request.getOfficeUserId());
        if (officeUser == null) {
            officeUser = officeUserRepository.findFirstByUserId(request.getUserId());
            return REUser.getDefaultInstance();
        }

        return convertOfficeUserToReUser(officeUser, loginInfo);
    }

    private REUser convertOfficeUserToReUser(OfficeUser officeUser, LoginInfo loginInfo){
        REUser.Builder reUser = REUser.newBuilder();
        reUser.setOfficeType(getOfficeType(officeUser));
        setAuthorityReUser(officeUser, reUser);

        //REUser -> reDepartment
        Department department = departmentRepository.findOne(officeUser.getDepartmentId());
        if (department != null) {
            String displayName = department.getDisplayName();
            String departmentName = department.getName();
            REDepartment reDepartment = REDepartment.newBuilder()
                    .setId(department.getId())
                    .setDisplayName(displayName.isEmpty() ? departmentName : displayName)
                    .setName(departmentName.isEmpty() ? displayName : departmentName)
                    .setPath(department.getPath())
                    .build();
            reUser.setDepartment(reDepartment);
        }

        // REUser -> account status
        reUser.setAccountStatuses(officeUser.getAccountStatuses());
        FuncAuthoritySet funcAuthority = officeUser.getFuncAuthority();
        if (funcAuthority != null) {
            reUser.setFuncAuthoritySet(funcAuthority.asAuthority());
        }

        if (officeUser.getContact() != null) {
            Contact contact = officeUser.getContact();
            setContactReUser(contact, reUser);

            if (loginInfo != null) {
                OfficeUser officeUserLogin =
                        officeUserRepository.findOne(loginInfo.getOfficeUserId());
                ManagementAuthority managementAuthority =
                        officeUserLogin.getManagementAuthority();

                if (contact.getMailAddressPublishingType() != null) {
                    if (PublishingType.ALL.name()
                            .equals(contact.getMailAddressPublishingType().name())) {
                        reUser.setMailAddress(Strings.nvl(officeUser.getMailAddress()));
                    } else if (PublishingType.INHOUSE.name()
                            .equals(contact.getMailAddressPublishingType().name())) {
                        if (officeUser.getOfficeId().equals(officeUserLogin.getOfficeId())) {
                            reUser.setMailAddress(Strings.nvl(officeUser.getMailAddress()));
                        }
                    } else if (PublishingType.PRIVATE.name()
                            .equals(contact.getMailAddressPublishingType().name())) {
                        if (officeUser.getUserId().equals(officeUserLogin.getUserId())) {
                            reUser.setMailAddress(Strings.nvl(officeUser.getMailAddress()));
                        }
                    }
                }

                if (contact.getMobileNoPublishingType() != null) {
                    if (PublishingType.ALL.name()
                            .equals(contact.getMobileNoPublishingType().name())) {
                        reUser.setMobileNo(Strings.nvl(contact.getMobileNo()));
                    } else if (PublishingType.INHOUSE.name()
                            .equals(contact.getMobileNoPublishingType().name())) {
                        if (officeUser.getOfficeId().equals(officeUserLogin.getOfficeId())) {
                            reUser.setMobileNo(Strings.nvl(contact.getMobileNo()));
                        }
                    } else if (PublishingType.PRIVATE.name()
                            .equals(contact.getMobileNoPublishingType().name())) {
                        if (officeUser.getUserId().equals(officeUserLogin.getUserId())) {
                            reUser.setMobileNo(Strings.nvl(contact.getMobileNo()));
                        }
                    }
                }
            } else {
                if (contact.getMailAddressPublishingType() != null && !PublishingType.PRIVATE.name()
                        .equals(contact.getMailAddressPublishingType().name())) {
                    // set mail address
                    reUser.setMailAddress(Strings.nvl(officeUser.getMailAddress()));
                }

                if (contact.getMobileNoPublishingType() != null && !PublishingType.PRIVATE.name()
                        .equals(contact.getMobileNoPublishingType().name())) {
                    reUser.setMobileNo(Strings.nvl(contact.getMobileNo()));
                }
            }

        }
        // REUser -> jobType
        reUser.setJobType(Strings.nvl(officeUser.getJobType()));
        if (officeUser.getJobType() != null && !officeUser.getJobType().isEmpty()) {
            reUser.setJobName(
                    Strings.nvl(grpcClientMasterService.getJobName(officeUser.getJobType())));
        }

        reUser.addAllSpecializedDepartment(commonService.getSpecializedDepartment(officeUser));

        Profile profile = officeUser.getProfile();
        if (profile != null) {
            setProfileReUser(profile, reUser);
        }

        // set birthDate
        if (officeUser.getBirthDate() != null) {
            reUser.setBirthDate(Dates.formatUTC(officeUser.getBirthDate()));
        }
        //set officeUserId
        reUser.setOfficeUserId(Strings.nvl(officeUser.getId()));
        //set userId
        reUser.setUserId(Strings.nvl(officeUser.getUserId()));
        // set firstName
        reUser.setFirstName(Strings.nvl(officeUser.getFirstName()));

        // set firstNameKana
        reUser.setFirtNameKana(Strings.nvl(officeUser.getFirstNameKana()));

        // set lastName
        reUser.setLastName(Strings.nvl(officeUser.getLastName()));

        // set lastNameKana
        reUser.setLastNameKana(Strings.nvl(officeUser.getLastNameKana()));

        // set additionalMailAddress
        if (officeUser.getAdditionalMailAddresses() != null) {
            reUser.addAllAdditionalMailAddress(officeUser.getAdditionalMailAddresses());
        }
        // set officeId
        reUser.setOfficeId(Strings.nvl(officeUser.getOfficeId()));
        // get office with officeId
        Office office = officeRepository.findOne(officeUser.getOfficeId());
        if (office != null) {
            // set officeName
            reUser.setOfficeName(office.getName());
        }

        if (officeUser instanceof MRUser) {
            MRUser mrUser = MRUser.class.cast(officeUser);
            List<HandlingHospital> handlingHospitals = mrUser.getHandleOffices();
            if (!CollectionUtils.isEmpty(handlingHospitals)) {

                handlingHospitals.forEach(handlingHospital -> {
                    REHandlingHospital.Builder builder = REHandlingHospital.newBuilder();

                    builder.setId(Strings.nvl(handlingHospital.getId()));
                    builder.setMrOfficeId(Strings.nvl(handlingHospital.getMrOfficeId()));
                    builder.setUserId(Strings.nvl(handlingHospital.getUserId()));

                    builder.setOfficeId(Strings.nvl(handlingHospital.getOfficeId()));
                    builder.setOfficeName(Strings.nvl(handlingHospital.getOfficeName()));
                    builder.setOtherHospital(handlingHospital.isOtherHospital());
                    builder.setOperator(Strings.nvl(handlingHospital.getOperator()));

                    reUser.addHandlingHospitals(builder);
                });
            }
        }

        // set login id
        reUser.setLoginId(Strings.nvl(officeUser.getLoginId()));

        // set new login id
        reUser.setNewLoginId(StringUtils.EMPTY);

        if (officeUser.getSettings() != null
                && officeUser.getSettings().getOfficeUserSettingId() != null) {
            OfficeUserSettings officeUserSettings = officeUserSettingsRepository.findFirstById(officeUser.getSettings().getOfficeUserSettingId().toString());
            if (officeUserSettings != null) {
                reUser.setHiddenRequiresResponse(officeUserSettings.isHiddenRequiresResponse());
            }
        }

        return reUser.build();
    }

    /**
     * Get list OfficeUser by ID
     *
     * @param request GRListOfficeUserByOfficeUserIdRequest
     * @return list OfficeUser
     */
    public REListUserByOfficeUserIdResponse getAllOfficeUserByOfficeUserId(REListUserByOfficeUserIdRequest request) {
        List<OfficeUser> officeUsers = officeUserRepository.findByIds(request.getOfficeUserIdList());
        REListUserByOfficeUserIdResponse.Builder reListUserByOfficeUserIdBuilder = REListUserByOfficeUserIdResponse.newBuilder();
        officeUsers.forEach(officeUser -> {
            reListUserByOfficeUserIdBuilder.addReUser(convertOfficeUserToREUser(officeUser));
        });
        return reListUserByOfficeUserIdBuilder.build();
    }

    /**
     * SSOトークンでユーザー情報を取得する
     */
    public REGetSSOTokenUserInfoResponse getSSOTokenUserInfo(LoginInfo loginInfo) {
        REGetSSOTokenUserInfoResponse.Builder builder = REGetSSOTokenUserInfoResponse.newBuilder();

        // LoginInfoからのデータ
        String userId = loginInfo.getUserId();
        String officeId = loginInfo.getOfficeId();
        String officeUserId = loginInfo.getOfficeUserId();

        if (Strings.isEmpty(userId) || Strings.isEmpty(officeId) || Strings.isEmpty(officeUserId)) {
            throw ServiceStatus.FAILED_PRECONDITION.withMessage("invalid login information")
                    .asStatusRuntimeException();
        }

        builder.setUserId(userId);
        OfficeUser officeUser = officeUserRepository.findOne(officeUserId);
        Office userOffice = officeRepository.findOne(officeId);

        // REUserからのデータ
        String firstName = officeUser.getFirstName();
        String lastName = officeUser.getLastName();
        String officeName = userOffice.getName();
        String jobType = officeUser.getJobType();
        String jobName = grpcClientMasterService.getJobName(officeUser.getJobType());

        builder.setFirstName(firstName);
        builder.setLastName(lastName);

        // OFFICE
        RESSOTokenUserInfoAttribute office = RESSOTokenUserInfoAttribute.newBuilder()
                .setAttributeId(officeId)
                .setType(RESSOTokenUserInfoAttribute.RESSOAttributeType.OFFICE)
                .setName(officeName)
                .build();
        builder.addAttributes(office);

        // GROUP
        GRListGroupBelongUserResponse groupBelongUserResponse =
                grpcClientGroupService.getUserGroups(officeUserId);
        List<RESSOTokenUserInfoAttribute> groups =
                groupBelongUserResponse.getGroupsList()
                        .stream()
                        .map(group -> RESSOTokenUserInfoAttribute.newBuilder()
                                .setAttributeId(group.getId())
                                .setType(RESSOTokenUserInfoAttribute.RESSOAttributeType.GROUP)
                                .setName(group.getName())
                                .build())
                        .collect(Collectors.toList());
        builder.addAllAttributes(groups);

        // ASSOCIATION
        // TODO

        // SPECIALIZED_DEPARTMENT
        List<RESSOTokenUserInfoAttribute> specializations =
                officeUser.getSpecializedDepartments().stream()
                        .map(specialization -> {
                            final String specializationId = specialization.getTypeId();
                            final String specializationName = grpcClientMasterService.getNameTypeById(specializationId);
                            return RESSOTokenUserInfoAttribute.newBuilder()
                                    .setAttributeId(specializationId)
                                    .setType(
                                            RESSOTokenUserInfoAttribute.RESSOAttributeType.SPECIALIZED_DEPARTMENT)
                                    .setName(specializationName)
                                    .build();
                        })
                        .collect(Collectors.toList());
        builder.addAllAttributes(specializations);

        // JOB
        RESSOTokenUserInfoAttribute job = RESSOTokenUserInfoAttribute.newBuilder()
                .setAttributeId(jobType)
                .setType(RESSOTokenUserInfoAttribute.RESSOAttributeType.JOB)
                .setName(jobName)
                .build();
        builder.addAttributes(job);

        return builder.build();
    }

    public REGetListUserByConditionResponse getListUserByCondition(REGetListUserByConditionRequest request) {
        List<OfficeUser> officeUsers = officeUserRepository.listUserByCondition(request);
        List<String> officeUserIds = new ArrayList<>();

        if (!CollectionUtils.isEmpty(officeUsers)) {
            officeUserIds = officeUsers.stream()
                    .map(OfficeUser::getId)
                    .collect(Collectors.toList());
        }

        return REGetListUserByConditionResponse.newBuilder()
                .addAllOfficeUserId(officeUserIds)
                .build();
    }

    /**
     * Get User info response grant service attendance
     *
     * @param request Input request
     * @return REGetUserGrantServiceAttendanceResponse
     */
    public REGetUserGrantServiceAttendanceResponse getUserGrantServiceAttendance(REGetUserGrantServiceAttendanceRequest request) {
        UserGrantServiceAttendanceCondition condition = new UserGrantServiceAttendanceCondition(request);
        List<OfficeUser> officeUsers = officeUserRepository.findUsersGrantServiceAttendance(condition);
        Map<String, Office> officeMap = new HashMap<>();
        Map<String, Department> departmentMap = new HashMap<>();
        Map<String, String> jobMap = new HashMap<>();
        List<REGetUserGrantServiceAttendanceResponse.REUserAttendance> reUserAttendances = new ArrayList<>();
        for (OfficeUser officeUser : officeUsers) {
            REGetUserGrantServiceAttendanceResponse.REUserAttendance.Builder reUserAttendance = REGetUserGrantServiceAttendanceResponse.REUserAttendance.newBuilder();
            Office office = null;
            if (officeMap.containsKey(officeUser.getOfficeId())) {
                office = officeMap.get(officeUser.getOfficeId());
            } else {
                office = officeRepository.findOfficeBy(officeUser.getOfficeId());
                officeMap.put(officeUser.getOfficeId(), office);
            }
            Department department = null;
            if (departmentMap.containsKey(officeUser.getDepartmentId())) {
                department = departmentMap.get(officeUser.getDepartmentId());
            } else {
                department = departmentRepository.findOne(officeUser.getDepartmentId());
                if (office != null) {
                    departmentMap.put(officeUser.getDepartmentId(), department);
                }
            }
            String jobName = "";
            if (jobMap.containsKey(officeUser.getJobType())) {
                jobName = jobMap.get(officeUser.getJobType());
            } else if (!Strings.isEmpty(officeUser.getJobType())) {
                jobName = grpcClientMasterService.getJobName(officeUser.getJobType());
                if (office != null) {
                    jobMap.put(officeUser.getJobType(), jobName);
                }
            }
            reUserAttendance.setOfficeUserId(officeUser.getId());
            reUserAttendance.setUserId(officeUser.getUserId());
            reUserAttendance.setDepartmentId(department.getId());
            reUserAttendance.setDepartmentName(department.getDisplayName().isEmpty() ? department.getName() : department.getDisplayName() );
            reUserAttendance.setFirstName(officeUser.getFirstName());
            reUserAttendance.setLastName(officeUser.getLastName());
            reUserAttendance.setFirstNameKana(officeUser.getFirstNameKana());
            reUserAttendance.setLastNameKana(officeUser.getLastNameKana());
            reUserAttendance.setJobType(officeUser.getJobType());
            reUserAttendance.setJobName(jobName);
            reUserAttendance.setAccountStatus(officeUser.getAccountStatuses());
            reUserAttendance.setCreated(Dates.format(officeUser.getCreated(), DATE_FORMAT_YYYYMMDD, TimeZone.getTimeZone("JST")));
            if(officeUser.getFp12ManagementLevel() != null)
            reUserAttendance.setFp12ManagementLevel(officeUser.getFp12ManagementLevel().asREManagementLevel());
            reUserAttendance.setFuncAuthority(officeUser.getFuncAuthority().asAuthorities());
            reUserAttendances.add(reUserAttendance.build());
        }
        return REGetUserGrantServiceAttendanceResponse.newBuilder().addAllUserAttendance(reUserAttendances).build();
    }

    /** ログインID・メールアドレス重複チェック*/
    private void checkDuplicate(OfficeUser officeUser) {

        // 一時領域を取得
        OfficeUser.Stash stash = officeUser.getStash();

        // ログインID重複チェック
        String loginId = Objects.nonNull(stash) ? stash.getLoginId() : officeUser.getLoginId();
        if (commonService.isUserExist(loginId, officeUser.getId())) {
            throw ServiceStatus.ALREADY_EXISTS.withMessage(
                    String.format("Exist loginId. loginId:%s", loginId))
                    .asStatusRuntimeException();
        }

        // 通知先メールアドレス重複チェック
        String mailAddress =
                Objects.nonNull(stash) ? stash.getMailAddress() : officeUser.getMailAddress();
        if (commonService.isExistMail(mailAddress, officeUser.getId())) {
            throw ServiceStatus.ALREADY_EXISTS.withMessage(
                    String.format("Exist mailAddress. maidAddress:%s", mailAddress))
                    .asStatusRuntimeException();
        }

        // 追加メールアドレス重複チェック
        List<String> additionalMailAddresses =
                Objects.nonNull(stash) ? stash.getAdditionalMailAddresses()
                        : officeUser.getAdditionalMailAddresses();
        if (!CollectionUtils.isEmpty(additionalMailAddresses)) {
            additionalMailAddresses.forEach(email -> {
                // check Duplicate mail address check in system other user
                if (StringUtils.isNotBlank(email)) {
                    if (commonService.isExistMail(email, officeUser.getId())) {
                        throw ServiceStatus.ALREADY_EXISTS.withMessage(
                                String.format("Exist mailAddress. maidAddress:%s", mailAddress))
                                .asStatusRuntimeException();
                    }
                }
            });
        }
    }


    /**
     * Get list officeUser and list userProvisionalEntry by list officeUserId
     *
     * @param request {@link REListUserByOfficeUserIdRequest}
     * @return list officeUser and list userProvisionalEntry
     */
    public RECountUserByListOfficeUserIdResponse countUserIsProvisionalAndValid(REListUserByOfficeUserIdRequest request) {
        long countUser = officeUserRepository.countByIdInAndAccountStatuses(request.getOfficeUserIdList(), Arrays.asList(1, 2));

        RECountUserByListOfficeUserIdResponse.Builder reCountUserByListOfficeUserIdResponse = RECountUserByListOfficeUserIdResponse.newBuilder();
        reCountUserByListOfficeUserIdResponse.setSumMemberOfGroup(countUser);

        return reCountUserByListOfficeUserIdResponse.build();
    }

    /**
     * convert OfficeUser to REUser by officeUser
     *
     * @param officeUser {@link OfficeUser}
     * @return officeUser
     */
    public REUser convertOfficeUserToREUser(OfficeUser officeUser) {
        REUser.Builder reUserBuilder = REUser.newBuilder();
        //set officeUserId
        reUserBuilder.setOfficeUserId(Strings.nvl(officeUser.getId()));
        reUserBuilder.setLoginId(Strings.nvl(officeUser.getLoginId()));
        reUserBuilder.setFirstName(Strings.nvl(officeUser.getFirstName()));
        reUserBuilder.setLastName(Strings.nvl(officeUser.getLastName()));
        reUserBuilder.setFirtNameKana(Strings.nvl(officeUser.getFirstNameKana()));
        reUserBuilder.setLastNameKana(Strings.nvl(officeUser.getLastNameKana()));
        reUserBuilder.setJobType(Strings.nvl(officeUser.getJobType()));
        reUserBuilder.setMailAddress(Strings.nvl(officeUser.getMailAddress()));
        if(officeUser.getProfile() != null) {
            reUserBuilder.setImageUrl(Strings.nvl(officeUser.getProfile().getImage()));
        }
        reUserBuilder.setOfficeId(Strings.nvl(officeUser.getOfficeId()));
        reUserBuilder.setAccountStatuses(officeUser.getAccountStatuses());
        reUserBuilder.setOfficeId(Strings.nvl(officeUser.getOfficeId()));
        reUserBuilder.setFuncAuthority(officeUser.getFuncAuthority().asAuthorities());
        Office office = officeRepository.findOne(officeUser.getOfficeId());
        if (office != null) {
            // set officeName
            reUserBuilder.setOfficeName(Strings.nvl(office.getName()));
        }
        Department department = departmentRepository.findOne(officeUser.getDepartmentId());
        if (department != null) {
            String displayName = department.getDisplayName();
            String departmentName = department.getName();
            REDepartment reDepartment = REDepartment.newBuilder()
                    .setId(department.getId())
                    .setDisplayName(StringUtils.defaultIfBlank(displayName, departmentName))
                    .setName(StringUtils.defaultIfBlank(departmentName, displayName))
                    .setPath(department.getPath())
                    .build();
            reUserBuilder.setDepartment(reDepartment);
        }
        if (officeUser.getFp12ManagementLevel() != null) {
            reUserBuilder.setFp12ManagementLevel(officeUser.getFp12ManagementLevel().asREManagementLevel());
        }

        return reUserBuilder.build();
    }

    public REGetUserGrantServiceAttendanceResponse getUserGrantServiceAttendanceFP15Department(
            REGetUserGrantServiceAttendanceRequest request) {
        UserGrantServiceAttendanceCondition condition = new UserGrantServiceAttendanceCondition(request);
        List<OfficeUser> officeUsers = officeUserRepository.getUserGrantServiceAttendanceFP15Department(condition);
        Map<String, Office> officeMap = new HashMap<>();
        Map<String, Department> departmentMap = new HashMap<>();
        Map<String, String> jobMap = new HashMap<>();
        List<REGetUserGrantServiceAttendanceResponse.REUserAttendance> reUserAttendances = new ArrayList<>();
        for (OfficeUser officeUser : officeUsers) {
            REGetUserGrantServiceAttendanceResponse.REUserAttendance.Builder reUserAttendance = REGetUserGrantServiceAttendanceResponse.REUserAttendance.newBuilder();
            Office office = null;
            if (officeMap.containsKey(officeUser.getOfficeId())) {
                office = officeMap.get(officeUser.getOfficeId());
            } else {
                office = officeRepository.findOfficeBy(officeUser.getOfficeId());
                officeMap.put(officeUser.getOfficeId(), office);
            }
            Department department = null;
            if (departmentMap.containsKey(officeUser.getDepartmentId())) {
                department = departmentMap.get(officeUser.getDepartmentId());
            } else {
                department = departmentRepository.findOne(officeUser.getDepartmentId());
                if (office != null) {
                    departmentMap.put(officeUser.getDepartmentId(), department);
                }
            }
            String jobName = "";
            if (jobMap.containsKey(officeUser.getJobType())) {
                jobName = jobMap.get(officeUser.getJobType());
            } else if (!Strings.isEmpty(officeUser.getJobType())) {
                jobName = grpcClientMasterService.getJobName(officeUser.getJobType());
                if (office != null) {
                    jobMap.put(officeUser.getJobType(), jobName);
                }
            }
            reUserAttendance.setOfficeUserId(officeUser.getId());
            reUserAttendance.setUserId(officeUser.getUserId());
            reUserAttendance.setDepartmentId(department.getId());
            reUserAttendance.setDepartmentName(department.getDisplayName().isEmpty() ? department.getName() : department.getDisplayName() );
            reUserAttendance.setFirstName(officeUser.getFirstName());
            reUserAttendance.setLastName(officeUser.getLastName());
            reUserAttendance.setFirstNameKana(officeUser.getFirstNameKana());
            reUserAttendance.setLastNameKana(officeUser.getLastNameKana());
            reUserAttendance.setJobType(officeUser.getJobType());
            reUserAttendance.setJobName(jobName);
            reUserAttendance.setAccountStatus(officeUser.getAccountStatuses());
            if(officeUser.getFp12ManagementLevel() != null)
                reUserAttendance.setFp12ManagementLevel(officeUser.getFp12ManagementLevel().asREManagementLevel());
            reUserAttendance.setFuncAuthority(officeUser.getFuncAuthority().asAuthorities());
            reUserAttendances.add(reUserAttendance.build());
        }
        return REGetUserGrantServiceAttendanceResponse.newBuilder().addAllUserAttendance(reUserAttendances).build();
    }

    public REGetListUserNameByListUserIdResponse getListUserNameByListOfficeUserId(
            REGetListUserNameByListUserIdRequest request) {

        REGetListUserNameByListUserIdResponse.Builder builder = REGetListUserNameByListUserIdResponse.newBuilder();

        Map<String, REStaffItem> map = new LinkedHashMap<>();

        List<OfficeUser> officeUsers = officeUserRepository.findByIdIn(request.getOfficeUserIdList());
        officeUsers.forEach(officeUser -> {
            REStaffItem.Builder newBuilder = REStaffItem.newBuilder();
            Department department =
                    departmentRepository.findById(officeUser.getDepartmentId())
                            .orElse(new Department());
            String departmentName = StringUtils.isNotBlank(department.getDisplayName()) ? department
                    .getDisplayName() : department.getName();
            newBuilder.setDepartment(Strings.nvl(departmentName));
            newBuilder.setFirstNameKana(Strings.nvl(officeUser.getFirstNameKana()));
            newBuilder.setLastNameKana(Strings.nvl(officeUser.getLastNameKana()));
            newBuilder.setLastName(Strings.nvl(officeUser.getLastName()));
            newBuilder.setFirtName(Strings.nvl(officeUser.getFirstName()));
            map.put(officeUser.getId(),newBuilder.build());

        });
        builder.putAllReUserName(map);
        return builder.build();
    }

    public REGetListDepartmentIdByOfficeIdResponse getListDepartmentIdByOfficeId(
            REGetListDepartmentIdByOfficeIdRequest request) {

        REGetListDepartmentIdByOfficeIdResponse.Builder builder = REGetListDepartmentIdByOfficeIdResponse.newBuilder();
        long count = officeUserRepository.countByOfficeIdAndFuncAuthorityFP15All(request.getOfficeId());
        Set<String> listDept = new HashSet<>();
        if(count > 0){
            builder.setCheckFp15ManagementLevelAll(true);
        } else {
            builder.setCheckFp15ManagementLevelAll(false);
        }
        List<OfficeUser> officeUsers = officeUserRepository.findByOfficeIdAndFuncAuthorityFP15Limited(request.getOfficeId());
        officeUsers.forEach(officeUser -> {
            Department department = departmentRepository.findOne(officeUser.getDepartmentId());
            if(department.getPath().equals(PATH_DEPARTMENT)){
                listDept.add(officeUser.getDepartmentId());
            } else {
                listDept.addAll(departmentService.getListDepartmentId(department));
            }
        });
        builder.addAllDepartmentId(listDept);
        return builder.build();
    }

    public REGetListUserByConditionResponse getListUserLevelLimitedByCondition(REGetListUserByConditionRequest request) {
        List<OfficeUser> officeUsers = officeUserRepository.getListUserLevelLimitedByCondition(request);
        List<String> officeUserIds = new ArrayList<>();

        if (!CollectionUtils.isEmpty(officeUsers)) {
            officeUserIds = officeUsers.stream()
                    .map(OfficeUser::getId)
                    .collect(Collectors.toList());
        }

        return REGetListUserByConditionResponse.newBuilder()
                .addAllOfficeUserId(officeUserIds)
                .build();
    }


    /**
     *
     * @param request
     * @return
     */
    public REGetListManagerRequestResponse getListManagerRequest(REGetListManagerRequestRequest request) {

        List<OfficeUser> officeUsers = officeUserRepository.getListManagerRequest(request);

        REGetListManagerRequestResponse.Builder builderListManagerRequest = REGetListManagerRequestResponse.newBuilder();
        builderListManagerRequest.
                setTotalRecords((int) officeUserRepository.countListManagerRequest(request));

        Set<String> listJobType = new HashSet<>();
        List<String> listOfficeUserId = new ArrayList<>();
        List<REManagerRequestItem> managerRequestItems = getListManagerRequest(officeUsers, listJobType, listOfficeUserId);
        builderListManagerRequest.addAllJobType(listJobType);
        builderListManagerRequest.addAllOfficeUserId(listOfficeUserId);
        builderListManagerRequest.addAllManagerRequestItem(managerRequestItems);

        return builderListManagerRequest.build();
    }

    /**
     *
     * @param officeUsers
     * @param listJobType
     * @param listOfficeUserId
     * @return
     */
    private  List<REManagerRequestItem> getListManagerRequest(List<OfficeUser> officeUsers,
                                                              Set<String> listJobType, List<String> listOfficeUserId) {

        List<REManagerRequestItem> managerRequestItems = new ArrayList<>();
        Map<String, Department> departmentMap = new HashMap<>();
        for (OfficeUser officeUser : officeUsers) {
            REManagerRequestItem.Builder builder = REManagerRequestItem.newBuilder();
            Department department = null;
            if (departmentMap.containsKey(officeUser.getDepartmentId())) {
                department = departmentMap.get(officeUser.getDepartmentId());
            } else {
                department = departmentRepository.findOne(officeUser.getDepartmentId());
                departmentMap.put(officeUser.getDepartmentId(), department);
            }
            listJobType.add(officeUser.getJobType());
            listOfficeUserId.add(officeUser.getId());
            builder.setOfficeUserId(officeUser.getId());
            builder.setManagerRequestId(officeUser.getId());
            builder.setDepartmentId(department.getId());
            builder.setDepartmentName(department.getDisplayName().isEmpty() ? department.getName() : department.getDisplayName() );
            builder.setFirstName(officeUser.getFirstName());
            builder.setLastName(officeUser.getLastName());
            builder.setFirstNameKana(officeUser.getFirstNameKana());
            builder.setLastNameKana(officeUser.getLastNameKana());
            builder.setJobType(officeUser.getJobType());
            builder.setAccountStatus(officeUser.getAccountStatuses());
            if(officeUser.getFp15ManagementLevel() != null)
                builder.setFp15ManagementLevel(officeUser.getFp15ManagementLevel().asREManagementLevel());
            builder.setFuncAuthority(officeUser.getFuncAuthority().asAuthorities());
            managerRequestItems.add(builder.build());
        }
        return  managerRequestItems;
    }

    public REGetListUserByConditionResponse getListUserByRequestManagementAuthorityCondition(
            REGetListUserByRequestManagementAuthorityConditionRequest request) {
        List<OfficeUser> officeUsers = officeUserRepository.getListUserByRequestManagementAuthorityCondition(request);
        List<String> officeUserIds = new ArrayList<>();

        if (!CollectionUtils.isEmpty(officeUsers)) {
            officeUserIds = officeUsers.stream()
                    .map(OfficeUser::getId)
                    .collect(Collectors.toList());
        }

        return REGetListUserByConditionResponse.newBuilder()
                .addAllOfficeUserId(officeUserIds)
                .build();
    }

    public REGetListDepartmentByOfficeIdResponse getListDepartmentByOfficeId(
            REGetListDepartmentByOfficeIdRequest request) {

        REGetListDepartmentByOfficeIdResponse.Builder builder = REGetListDepartmentByOfficeIdResponse.newBuilder();
        List<Department> departments = departmentRepository.findByIds(request.getDepartmentIdList());

        List<REListDepartmentId> listDepartmentId = new ArrayList<>();
        departments.forEach(department -> {
            REListDepartmentId.Builder newBuilder = REListDepartmentId.newBuilder();
            newBuilder.setDepartmentId(department.getId());
            if (!DEPARTMENT_ROOT.equals(department.getPath())) {
                List<String> departmentChildrens = new ArrayList<>();
                department.getChildren().forEach(departmentChildren -> {
                    departmentChildrens.add(departmentChildren.getId());
                });
                newBuilder.addAllDepartmentIdChildren(departmentChildrens);
            }

            listDepartmentId.add(newBuilder.build());
        });
        builder.addAllListDepartmentId(listDepartmentId);
        return builder.build();
    }

    /**
     * Get list OfficeUser by username, deparmentId, jobtype, officeId and accountStatus
     *
     * @param request {@link REGetListOfficeUserByConditionRequest}
     * @return {@@link REGetListOfficeUserByConditionResponse}
     */
    public REGetListOfficeUserByConditionResponse getListOfficeUserByCondition(REGetListOfficeUserByConditionRequest request) {
        REGetListOfficeUserByConditionResponse.Builder response = REGetListOfficeUserByConditionResponse.newBuilder();
        OfficeUser user = officeUserRepository.findOne(request.getLoginOfficeUserId());
        Department department = departmentRepository.findFirstById(user.getDepartmentId());
        List<OfficeUser> officeUsers = officeUserRepository.getListOfficeUserByCondition(request, user, department);

        if (CollectionUtils.isNotEmpty(officeUsers)) {
            officeUsers.forEach(officeUser -> response.addUser(convertOfficeUserToREUser(officeUser)));
        }

        return response.build();
    }

    /**
     *  Get list officeUser by officeId
     *
     * @param request {@link REGetOfficeUsersByOfficeIdRequest}
     * @return {@link REGetOfficeUsersByOfficeIdResponse}
     */
    public REGetOfficeUsersByOfficeIdResponse getOfficeUsersByOfficeId(REGetOfficeUsersByOfficeIdRequest request) {
        List<OfficeUser> officeUsers = officeUserRepository.findByOfficeId(request.getOfficeId());

        REGetOfficeUsersByOfficeIdResponse.Builder builder = REGetOfficeUsersByOfficeIdResponse.newBuilder();

        if (CollectionUtils.isNotEmpty(officeUsers)) {
            officeUsers.forEach(officeUser -> builder.addUser(convertOfficeUserToREUser(officeUser)));
        }

        return builder.build();
    }

    /**
     * オフィスIDから、オフィスユーザーIDリストを取得し、オフィスユーザーIDリストから、ユーザーIDリストを取得し、返却する
     *
     * @param request オフィスID
     * @return ユーザーIDリスト
     */
    public REGetUserIdListByOfficeIdResponse getUserIdListByOfficeId(
            REGetUserIdListByOfficeIdRequest request) {
        String officeId = request.getOfficeId();

        List<String> userIdList = officeUserRepository.findByOfficeId(officeId).stream()
                .map(OfficeUser::getUserId)
                .collect(Collectors.toList());

        REGetUserIdListByOfficeIdResponse response = REGetUserIdListByOfficeIdResponse.newBuilder()
                .addAllUserIdList(userIdList)
                .build();

        return response;
    }

    //phuclq
    public ListStudentResponse addStudent(SaveStudentRequest request) {
        ListStudentResponse.Builder reponse = ListStudentResponse.newBuilder();
        Student student = new Student(request.getName(), request.getAge(), request.getAddress(), request.getGpa());
        studentRepository.save(student);
        List<Student> studentList = studentRepository.findAll();
        List<StudentResponse> studentResponseList = studentList.stream().map(Student::asStudentResponse).collect(Collectors.toList());
        return reponse.addAllStudent(studentResponseList).build();
    }

    public ListStudentResponse editStudent(EditStudentRequest request) {
        ListStudentResponse.Builder reponse = ListStudentResponse.newBuilder();
        Student studentEdit = studentRepository.findOne(request.getId());
        if (studentEdit != null) {
            studentEdit.setName(request.getName());
            studentEdit.setAge(request.getAge());
            studentEdit.setAddress(request.getAddress());
            studentEdit.setGpa(request.getGpa());
        }
        List<Student> studentList = studentRepository.findAll();
        List<StudentResponse> studentResponseList = studentList.stream().map(Student::asStudentResponse).collect(Collectors.toList());
        return reponse.addAllStudent(studentResponseList).build();
    }

    public void delStudent(DelStudentRequest request) {
        studentRepository.deleteById(request.getId());
    }

    public ListStudentResponse sortByNameStudent(Empty empty) {
        ListStudentResponse.Builder reponse = ListStudentResponse.newBuilder();
        List<Student> studentListOrderName = studentRepository.findByOrderByNameAsc();
        List<StudentResponse> studentResponseList = studentListOrderName.stream().map(Student::asStudentResponse).collect(Collectors.toList());
        return reponse.addAllStudent(studentResponseList).build();
    }

    public ListStudentResponse sortByGpaStudent(Empty empty) {
        ListStudentResponse.Builder reponse = ListStudentResponse.newBuilder();
        List<Student> studentListOrderGpa =studentRepository.findByOrderByGpaAsc();
        List<StudentResponse> studentResponseList = studentListOrderGpa.stream().map(Student::asStudentResponse).collect(Collectors.toList());
        return reponse.addAllStudent(studentResponseList).build();
    }

    public ListStudentResponse showStudent(Empty empty) {
        ListStudentResponse.Builder reponse = ListStudentResponse.newBuilder();
        studentRepository.findAll();
        return reponse.build();
    }
}

