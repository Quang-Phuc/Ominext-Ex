package jp.drjoy.backend.registration.rpc;

import com.google.protobuf.Empty;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import jp.drjoy.backend.registration.service.BlockUserService;
import jp.drjoy.backend.registration.service.ConferenceRoomService;
import jp.drjoy.backend.registration.service.DepartmentService;
import jp.drjoy.backend.registration.service.FunctionService;
import jp.drjoy.backend.registration.service.HandlingHospitalService;
import jp.drjoy.backend.registration.service.InvitationService;
import jp.drjoy.backend.registration.service.MailService;
import jp.drjoy.backend.registration.service.MeetingConfigureService;
import jp.drjoy.backend.registration.service.MrShareInfoStatusService;
import jp.drjoy.backend.registration.service.NotificationSettingService;
import jp.drjoy.backend.registration.service.OfficeFunctionService;
import jp.drjoy.backend.registration.service.OfficeService;
import jp.drjoy.backend.registration.service.OfficeUserRoleService;
import jp.drjoy.backend.registration.service.OfficeUserService;
import jp.drjoy.backend.registration.service.PasswordService;
import jp.drjoy.backend.registration.service.PrUserService;
import jp.drjoy.backend.registration.service.RegisterKeyCodeService;
import jp.drjoy.backend.registration.service.RegisterPersonalService;
import jp.drjoy.backend.registration.service.RoleService;
import jp.drjoy.backend.registration.service.SideMenuSettingService;
import jp.drjoy.backend.registration.service.UserService;
import jp.drjoy.backend.registration.service.UserSessionService;
import jp.drjoy.core.autogen.grpc.registration.DelStudentRequest;
import jp.drjoy.core.autogen.grpc.registration.EditStudentRequest;
import jp.drjoy.core.autogen.grpc.registration.GetOfficeRequest;
import jp.drjoy.core.autogen.grpc.registration.GetOfficeUserRequest;
import jp.drjoy.core.autogen.grpc.registration.ListOfficeUsersRequest;
import jp.drjoy.core.autogen.grpc.registration.ListOfficeUsersResponse;
import jp.drjoy.core.autogen.grpc.registration.ListOfficesRequest;
import jp.drjoy.core.autogen.grpc.registration.ListOfficesResponse;
import jp.drjoy.core.autogen.grpc.registration.ListStudentResponse;
import jp.drjoy.core.autogen.grpc.registration.OfficeMessage;
import jp.drjoy.core.autogen.grpc.registration.OfficeUserMessage;
import jp.drjoy.core.autogen.grpc.registration.REAddListUserConnectionRequest;
import jp.drjoy.core.autogen.grpc.registration.REAllowDrSeenRequest;
import jp.drjoy.core.autogen.grpc.registration.REAllowDrSeenResponse;
import jp.drjoy.core.autogen.grpc.registration.RECheckHandlingHospitalForListGroupRequest;
import jp.drjoy.core.autogen.grpc.registration.RECheckHandlingHospitalForListGroupResponse;
import jp.drjoy.core.autogen.grpc.registration.RECheckHandlingHospitalRequest;
import jp.drjoy.core.autogen.grpc.registration.RECheckHandlingHospitalResponse;
import jp.drjoy.core.autogen.grpc.registration.RECheckPasswordRequest;
import jp.drjoy.core.autogen.grpc.registration.RECheckPasswordResponse;
import jp.drjoy.core.autogen.grpc.registration.REConferenceRoom;
import jp.drjoy.core.autogen.grpc.registration.RECountUserByListOfficeUserIdResponse;
import jp.drjoy.core.autogen.grpc.registration.RECreateHandlingHospitalsRequest;
import jp.drjoy.core.autogen.grpc.registration.RECreateUserRequest;
import jp.drjoy.core.autogen.grpc.registration.RECreateUserResponse;
import jp.drjoy.core.autogen.grpc.registration.REDeleteHandlingHospitalsRequest;
import jp.drjoy.core.autogen.grpc.registration.REDeleteListUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REDeleteUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REDownloadProvisionalUsersResponse;
import jp.drjoy.core.autogen.grpc.registration.REFindAvailableConferenceRoomsRequest;
import jp.drjoy.core.autogen.grpc.registration.REFindAvailableConferenceRoomsResponse;
import jp.drjoy.core.autogen.grpc.registration.REForgetPasswordRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetAdditionalMailAddressChangeReservationRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetAdditionalMailAddressChangeReservationResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetBuildingAndConferenceRoomSettingRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetBuildingsAndConferenceRoomsSettingRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetBuildingsAndConferenceRoomsSettingResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetDepartmentResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetDepartmentsByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetFunctionsRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetFunctionsResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListMrByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListMrByOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListDepartmentByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListDepartmentByOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListDepartmentChildrenByIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListDepartmentChildrenByIdRequestResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListDepartmentIdByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListDepartmentIdByOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListDrWithConditionRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListDrWithConditionResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListManagerRequestRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListManagerRequestResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListOfficeIdHandlingHospitalRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListOfficeIdHandlingHospitalResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListOfficeRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListOfficeResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListOfficeUserByConditionRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListOfficeUserByConditionResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListOfficeUserIdsByOfficeIdsResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListRoleCodeRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListRoleCodeResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserByConditionRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserByConditionResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserByOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserByRequestManagementAuthorityConditionRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserNameByListUserIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserNameByListUserIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetMROfficeUserIdByNameRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetMROfficeUserIdByNameResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserNameByListUserIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetListUserNameByListUserIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetMailAddressChangeReservationRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetMailAddressChangeReservationResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetMrOfficeUserIdsByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetMrOfficeUserIdsByOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetMrUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetMrUserResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetOfficeUsersByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetOfficeUsersByOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetOfficeNamesByOfficeUserIdsRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetOfficeNamesByOfficeUserIdsResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetPrUserByOfficeUserIdOrUserIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetPrUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetRolesRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetRolesResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetStaffRequestByManagerIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetStaffRequestByManagerIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetStaffRequestItemRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetStaffRequestItemResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetUseEntryResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetUserByOfficeUserIdOrUserIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserByOfficeUserIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserEntryRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserIdListByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserIdListByOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REGetUserListRequest;
import jp.drjoy.core.autogen.grpc.registration.REGetUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REInvitePrUsersRequest;
import jp.drjoy.core.autogen.grpc.registration.REInvitePrUsersResponse;
import jp.drjoy.core.autogen.grpc.registration.REInviteUsersListRequest;
import jp.drjoy.core.autogen.grpc.registration.REInviteUsersResponse;
import jp.drjoy.core.autogen.grpc.registration.REListAsigneesHistoryRequest;
import jp.drjoy.core.autogen.grpc.registration.REListAsigneesHistoryResponse;
import jp.drjoy.core.autogen.grpc.registration.REListAsigneesRequest;
import jp.drjoy.core.autogen.grpc.registration.REListAsigneesResponse;
import jp.drjoy.core.autogen.grpc.registration.REListBlockUsersRequest;
import jp.drjoy.core.autogen.grpc.registration.REListBlockUsersResponse;
import jp.drjoy.core.autogen.grpc.registration.REListHandlingHospitalsByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REListHandlingHospitalsNowAndPastRequest;
import jp.drjoy.core.autogen.grpc.registration.REListHandlingHospitalsNowAndPastResponse;
import jp.drjoy.core.autogen.grpc.registration.REListHandlingHospitalsRequest;
import jp.drjoy.core.autogen.grpc.registration.REListHandlingHospitalsResponse;
import jp.drjoy.core.autogen.grpc.registration.REListHandlingHospitalsWithHistoryRequest;
import jp.drjoy.core.autogen.grpc.registration.REListMRUserHandleOfficeRequest;
import jp.drjoy.core.autogen.grpc.registration.REListMRUserHandleOfficeResponse;
import jp.drjoy.core.autogen.grpc.registration.REListMailUnConfirmAdditionalRequest;
import jp.drjoy.core.autogen.grpc.registration.REListMailUnConfirmAdditionalResponse;
import jp.drjoy.core.autogen.grpc.registration.REListMedicalOfficeUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REListMedicalOfficeUserResponse;
import jp.drjoy.core.autogen.grpc.registration.REListMeetingConfigureRequest;
import jp.drjoy.core.autogen.grpc.registration.REListMeetingConfigureResponse;
import jp.drjoy.core.autogen.grpc.registration.REListMrShareInfoStatusRequest;
import jp.drjoy.core.autogen.grpc.registration.REListMrShareInfoStatusResponse;
import jp.drjoy.core.autogen.grpc.registration.REListOfficeByKeywordRequest;
import jp.drjoy.core.autogen.grpc.registration.REListOfficeByListOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REListOfficeByListOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REListOfficeByNameRequest;
import jp.drjoy.core.autogen.grpc.registration.REListOfficeByNameResponse;
import jp.drjoy.core.autogen.grpc.registration.REListOfficeIdAndUserIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REListOfficeUserByOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REListOfficeUserByOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REListOfficeUserIdByIndustryRequest;
import jp.drjoy.core.autogen.grpc.registration.REListOfficeUserIdByIndustryResponse;
import jp.drjoy.core.autogen.grpc.registration.REListPrUser;
import jp.drjoy.core.autogen.grpc.registration.REListPrUserByIdsRequest;
import jp.drjoy.core.autogen.grpc.registration.REListPrUsersRequest;
import jp.drjoy.core.autogen.grpc.registration.REListPrUsersResponse;
import jp.drjoy.core.autogen.grpc.registration.REListProvisionalUsersRequest;
import jp.drjoy.core.autogen.grpc.registration.REListProvisionalUsersResponse;
import jp.drjoy.core.autogen.grpc.registration.REListStaffsRequest;
import jp.drjoy.core.autogen.grpc.registration.REListStaffsResponse;
import jp.drjoy.core.autogen.grpc.registration.REListUser;
import jp.drjoy.core.autogen.grpc.registration.REListUserByOfficeUserIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REListUserByOfficeUserIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REListUserSpecifiedAuthorityRequest;
import jp.drjoy.core.autogen.grpc.registration.REListUserSpecifiedAuthorityResponse;
import jp.drjoy.core.autogen.grpc.registration.REListUsersRequest;
import jp.drjoy.core.autogen.grpc.registration.REListUsersResponse;
import jp.drjoy.core.autogen.grpc.registration.REListVisitableUsersRequest;
import jp.drjoy.core.autogen.grpc.registration.REListVisitableUsersResponse;
import jp.drjoy.core.autogen.grpc.registration.RELockMRUserRequest;
import jp.drjoy.core.autogen.grpc.registration.RELockUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REMedicalOffice;
import jp.drjoy.core.autogen.grpc.registration.REMeetingConfigure;
import jp.drjoy.core.autogen.grpc.registration.RENotificationSettings;
import jp.drjoy.core.autogen.grpc.registration.REOfficeIdAndUserIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REPersonalUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REPersonalUserResponse;
import jp.drjoy.core.autogen.grpc.registration.REPharmacyOfficeInfo;
import jp.drjoy.core.autogen.grpc.registration.REPrOfficesByDrOfficeIdRequest;
import jp.drjoy.core.autogen.grpc.registration.REPrOfficesByDrOfficeIdResponse;
import jp.drjoy.core.autogen.grpc.registration.REPrUser;
import jp.drjoy.core.autogen.grpc.registration.REPrUserItem;
import jp.drjoy.core.autogen.grpc.registration.REPutDepartmentRequest;
import jp.drjoy.core.autogen.grpc.registration.REPutIdentifyStatusRequest;
import jp.drjoy.core.autogen.grpc.registration.REPutMeetingRestrictionRequest;
import jp.drjoy.core.autogen.grpc.registration.REPutMrShareInfoStatusRequest;
import jp.drjoy.core.autogen.grpc.registration.REPutMrShareInfoStatusResponse;
import jp.drjoy.core.autogen.grpc.registration.RERegisterKeyCodeRequest;
import jp.drjoy.core.autogen.grpc.registration.RERemoveMrAdoptedDrugsRequest;
import jp.drjoy.core.autogen.grpc.registration.REResetPasswordRequest;
import jp.drjoy.core.autogen.grpc.registration.RESaveBuildingsAndConferenceRoomsSettingRequest;
import jp.drjoy.core.autogen.grpc.registration.RESaveBuildingsAndConferenceRoomsSettingResponse;
import jp.drjoy.core.autogen.grpc.registration.RESaveFunctionRequest;
import jp.drjoy.core.autogen.grpc.registration.RESaveOfficeFunctionRequest;
import jp.drjoy.core.autogen.grpc.registration.RESaveRolesRequest;
import jp.drjoy.core.autogen.grpc.registration.RESendMailRequest;
import jp.drjoy.core.autogen.grpc.registration.RESendMailResponse;
import jp.drjoy.core.autogen.grpc.registration.RESideMenuSettings;
import jp.drjoy.core.autogen.grpc.registration.REUnlockMRUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REUnlockUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REUpdateAdditionalMailAddressRequest;
import jp.drjoy.core.autogen.grpc.registration.REUpdateImageProfileOnlyRequest;
import jp.drjoy.core.autogen.grpc.registration.REUpdateImageProfileRequest;
import jp.drjoy.core.autogen.grpc.registration.REUpdateMailAddressRequest;
import jp.drjoy.core.autogen.grpc.registration.REUpdateMailAddressResponse;
import jp.drjoy.core.autogen.grpc.registration.REUpdatePasswordRequest;
import jp.drjoy.core.autogen.grpc.registration.REUser;
import jp.drjoy.core.autogen.grpc.registration.REUserListRequest;
import jp.drjoy.core.autogen.grpc.registration.REUserListRespone;
import jp.drjoy.core.autogen.grpc.registration.REUserRequest;
import jp.drjoy.core.autogen.grpc.registration.REUserResponse;
import jp.drjoy.core.autogen.grpc.registration.REUserSessionResponse;
import jp.drjoy.core.autogen.grpc.registration.RegistrationGrpc;
import jp.drjoy.core.autogen.grpc.registration.SaveStudentRequest;
import jp.drjoy.core.autogen.grpc.registration.UpdateImageProfileAndIdentificationImageRequest;
import jp.drjoy.service.framework.grpc.GrpcGlobals;
import jp.drjoy.service.framework.grpc.GrpcHeaderServerInterceptor;
import jp.drjoy.service.framework.security.model.LoginInfo;
import jp.drjoy.service.framework.security.model.UserAuthorityInfo;
import jp.drjoy.spring.boot.grpc.server.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GrpcService(value = RegistrationGrpc.class, interceptors = GrpcHeaderServerInterceptor.class)
public class RegistrationGrpcServer extends RegistrationGrpc.RegistrationImplBase {

    private final Logger logger = LoggerFactory.getLogger(RegistrationGrpcServer.class);
    public static final boolean UNLOCK_USER_FLG = false;
    public static final boolean LOCK_USER_FLG = true;

    private final UserService userService;
    private final PasswordService passwordService;
    private final NotificationSettingService notificationSettingService;
    private final SideMenuSettingService sideMenuSettingService;
    private final HandlingHospitalService handlingHospitalService;
    private final InvitationService invitationService;
    private final DepartmentService departmentService;
    private final RegisterKeyCodeService registerKeyCodeService;
    private final MailService mailService;
    private final PrUserService prUserService;
    private final UserSessionService userSessionService;
    private final BlockUserService blockedUserService;
    private final MeetingConfigureService meetingConfigureService;
    private final OfficeService officeService;
    private final OfficeUserService officeUserService;
    private final RegisterPersonalService personalService;
    private final MrShareInfoStatusService mrShareInfoStatusService;
    private final FunctionService functionService;
    private final RoleService roleService;
    private final OfficeFunctionService officeFunctionService;
    private final OfficeUserRoleService officeUserRoleService;
    private final ConferenceRoomService conferenceRoomService;

    public RegistrationGrpcServer(final UserService userService,
                                  final PasswordService passwordService,
                                  final NotificationSettingService notificationSettingService,
                                  final SideMenuSettingService sideMenuSettingService,
                                  final HandlingHospitalService handlingHospitalService,
                                  final InvitationService invitationService,
                                  final DepartmentService departmentService,
                                  final RegisterKeyCodeService registerKeyCodeService,
                                  final MailService mailService,
                                  final PrUserService prUserService,
                                  final UserSessionService userSessionService,
                                  final MeetingConfigureService meetingConfigureService,
                                  final OfficeService officeService,
                                  final OfficeUserService officeUserService,
                                  final BlockUserService blockedUserService,
                                  final RegisterPersonalService personalService,
                                  final MrShareInfoStatusService mrShareInfoStatusService,
                                  final FunctionService functionService,
                                  final RoleService roleService,
                                  final OfficeFunctionService officeFunctionService,
                                  final OfficeUserRoleService officeUserRoleService,
                                  final ConferenceRoomService conferenceRoomService) {
        this.userService = userService;
        this.passwordService= passwordService;
        this.notificationSettingService = notificationSettingService;
        this.sideMenuSettingService = sideMenuSettingService;
        this.handlingHospitalService = handlingHospitalService;
        this.invitationService = invitationService;
        this.departmentService = departmentService;
        this.registerKeyCodeService =registerKeyCodeService;
        this.mailService = mailService;
        this.prUserService = prUserService;
        this.userSessionService = userSessionService;
        this.meetingConfigureService = meetingConfigureService;
        this.officeService = officeService;
        this.officeUserService = officeUserService;
        this.blockedUserService = blockedUserService;
        this.personalService = personalService;
        this.mrShareInfoStatusService = mrShareInfoStatusService;
        this.functionService = functionService;
        this.roleService = roleService;
        this.officeFunctionService = officeFunctionService;
        this.officeUserRoleService = officeUserRoleService;
        this.conferenceRoomService = conferenceRoomService;
    }

    // UserSession
    @Override public void getUserSession(Empty request, StreamObserver<REUserSessionResponse> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        responseObserver.onNext(userSessionService.getUserSession(loginInfo));
        responseObserver.onCompleted();
    }

    @Override public void updatePassword(REUpdatePasswordRequest request,
            StreamObserver<Empty> responseObserver) {
        // LoginInfo
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        // Update password
        passwordService.updatePassword(request, loginInfo);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getNotificationSettings(Empty request, StreamObserver<RENotificationSettings> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        RENotificationSettings settings = notificationSettingService.getNotificationSettings(loginInfo);
        responseObserver.onNext(settings);
        responseObserver.onCompleted();
    }

    @Override
    public void putNotificationSettings(RENotificationSettings request, StreamObserver<Empty> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        notificationSettingService.putNotificationSettings(request, loginInfo);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override public void getSideMenuSettings(Empty request,
        StreamObserver<RESideMenuSettings> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        responseObserver.onNext(sideMenuSettingService.getSideMenuSetting(loginInfo));
        responseObserver.onCompleted();

    }

    @Override public void putSideMenuSettings(RESideMenuSettings request,
        StreamObserver<Empty> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        sideMenuSettingService.putSideMenuSettings(request,loginInfo);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override public void getDepartments(Empty request,
                                         StreamObserver<REGetDepartmentResponse> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        REGetDepartmentResponse departments = departmentService.getDepartments(loginInfo);
        responseObserver.onNext(departments);
        responseObserver.onCompleted();
    }

    @Override public void getDepartmentsByOfficeId(
            REGetDepartmentsByOfficeIdRequest request,
            StreamObserver<REGetDepartmentResponse> responseObserver
    ) {
        String officeId = request.getOfficeId();
        REGetDepartmentResponse departments = departmentService.getDepartments(officeId);
        responseObserver.onNext(departments);
        responseObserver.onCompleted();
    }

    @Override public void putDepartments(REPutDepartmentRequest request,
        StreamObserver<Empty> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        departmentService.putDepartments(loginInfo, request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override public void listOffices(ListOfficesRequest request,
            StreamObserver<ListOfficesResponse> responseObserver) {
        responseObserver.onNext(officeService.listOffices(request));
        responseObserver.onCompleted();
    }

    @Override public void getOffice(GetOfficeRequest request,
            StreamObserver<OfficeMessage> responseObserver) {
        responseObserver.onNext(officeService.getOffice(request));
        responseObserver.onCompleted();
    }

    @Override public void listOfficeUsers(ListOfficeUsersRequest request,
            StreamObserver<ListOfficeUsersResponse> responseObserver) {
        responseObserver.onNext(officeUserService.listOfficeUsers(request));
        responseObserver.onCompleted();
    }

    @Override public void getOfficeUser(GetOfficeUserRequest request,
            StreamObserver<OfficeUserMessage> responseObserver) {
        responseObserver.onNext(officeUserService.getOfficeUser(request));
        responseObserver.onCompleted();
    }

    @Override public void listUsers(REListUsersRequest request,
            StreamObserver<REListUsersResponse> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        responseObserver.onNext(userService.listUser(request, loginInfo));
        responseObserver.onCompleted();
    }

    @Override public void getUser(REGetUserRequest request, StreamObserver<REUser> responseObserver) {

        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        responseObserver.onNext(userService.getUser(request ,loginInfo));
        responseObserver.onCompleted();
    }

    @Override public void putUser(REUser request, StreamObserver<REUserResponse> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        responseObserver.onNext(userService.putUser(request, loginInfo));
        responseObserver.onCompleted();
    }

    @Override
    public void unlockUser(REUnlockUserRequest request, StreamObserver<Empty> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        userService.lockOrUnlockUser(request.getUserId(),request.getOfficeId(),false, loginInfo);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void lockUser(RELockUserRequest request, StreamObserver<Empty> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        userService.lockOrUnlockUser(request.getUserId(),request.getOfficeId(),true, loginInfo);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void unlockMRUser(REUnlockMRUserRequest request, StreamObserver<Empty> responseObserver) {
        userService.lockOrUnlockMRUser(request.getOfficeUserId(),
                request.getLoginOfficeUserId(),
                request.getLoginUserId(),
                request.getLoginOfficeId(),
                UNLOCK_USER_FLG);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void lockMRUser(RELockMRUserRequest request, StreamObserver<Empty> responseObserver) {
        userService.lockOrUnlockMRUser(request.getOfficeUserId(),
                request.getLoginOfficeUserId(),
                request.getLoginUserId(),
                request.getLoginOfficeId(),
                LOCK_USER_FLG);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(REDeleteUserRequest request, StreamObserver<Empty> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        userService.deleteUser(request, loginInfo);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteListUser(REDeleteListUserRequest request, StreamObserver<Empty> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        userService.deleteListUser(request, loginInfo);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getMailAddressChangeReservation(REGetMailAddressChangeReservationRequest request,
            StreamObserver<REGetMailAddressChangeReservationResponse> responseObserver) {
        responseObserver.onNext(mailService.getMailAddressChangeReservation(request));
        responseObserver.onCompleted();
    }

    @Override public void updateMailAddress(REUpdateMailAddressRequest request,
            StreamObserver<REUpdateMailAddressResponse> responseObserver) {
        REUpdateMailAddressResponse response = mailService.updateMailAddress(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override public void getAdditionalMailAddressChangeReservation(
            REGetAdditionalMailAddressChangeReservationRequest request,
            StreamObserver<REGetAdditionalMailAddressChangeReservationResponse> responseObserver) {
        responseObserver.onNext(mailService.getAdditionMailChange(request));
        responseObserver.onCompleted();
    }

    @Override public void updateAdditionalMailAddress(REUpdateAdditionalMailAddressRequest request,
            StreamObserver<REUpdateMailAddressResponse> responseObserver) {
        REUpdateMailAddressResponse response = mailService.updateAdditionalMailAddress(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override public void registerKeyCode(RERegisterKeyCodeRequest request,
            StreamObserver<Empty> responseObserver) {
        registerKeyCodeService.regiterKeycode(request);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override public void getUserEntry(REGetUserEntryRequest request,
            StreamObserver<REGetUseEntryResponse> responseObserver) {

        REGetUseEntryResponse reGetUseEntryResponse = userService.getUserEntry(request);
        responseObserver.onNext(reGetUseEntryResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void createUser(RECreateUserRequest request, StreamObserver<RECreateUserResponse> responseObserver) {
        responseObserver.onNext( userService.createUserEntry(request));
        responseObserver.onCompleted();
    }

    @Override public void listStaffs(REListStaffsRequest request,
            StreamObserver<REListStaffsResponse> responseObserver) {

        responseObserver.onNext(REListStaffsResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }

    /**
     * listHandlingHospitals
     * @param request Empty
     * @param responseObserver StreamObserver
     */
    @Override public void listHandlingHospitals(REListHandlingHospitalsRequest request,
                                                StreamObserver<REListHandlingHospitalsResponse> responseObserver) {
        // Get Handling Hospitals
        List<REMedicalOffice> list = handlingHospitalService.listHandlingHospitals(request);

        if (!list.isEmpty()){
            // REListHandlingHospitalsResponse
            REListHandlingHospitalsResponse response = REListHandlingHospitalsResponse.newBuilder()
                    .addAllHandlingHostpitals(list)
                    .build();

            responseObserver.onNext(response);
        } else {
            responseObserver.onNext(REListHandlingHospitalsResponse.getDefaultInstance());
        }
        responseObserver.onCompleted();
    }

    /**
     * Get list handling hospital with history
     * @param request Empty
     * @param responseObserver StreamObserver
     */
    @Override public void listHandlingHospitalsWithHistory(REListHandlingHospitalsWithHistoryRequest request,
                                                           StreamObserver<REListHandlingHospitalsResponse> responseObserver) {
        // Get Handling Hospitals
        List<REMedicalOffice> list = handlingHospitalService.listHandlingHospitalsWithHistory(request);

        if (!list.isEmpty()){
            // REListHandlingHospitalsResponse
            REListHandlingHospitalsResponse response = REListHandlingHospitalsResponse.newBuilder()
                    .addAllHandlingHostpitals(list)
                    .build();

            responseObserver.onNext(response);
        } else {
            responseObserver.onNext(REListHandlingHospitalsResponse.getDefaultInstance());
        }
        responseObserver.onCompleted();
    }

    @Override
     public void listHandlingHospitalsByOfficeId(REListHandlingHospitalsByOfficeIdRequest request, StreamObserver<REListHandlingHospitalsResponse> responseObserver){
        List<REMedicalOffice> list = handlingHospitalService.listHandlingHospitals(request);
        if (!list.isEmpty()){
            // REListHandlingHospitalsResponse
            REListHandlingHospitalsResponse response = REListHandlingHospitalsResponse.newBuilder()
                    .addAllHandlingHostpitals(list)
                    .build();

            responseObserver.onNext(response);
        } else {
            responseObserver.onNext(REListHandlingHospitalsResponse.getDefaultInstance());
        }
        responseObserver.onCompleted();
     }

    @Override public void createHandlingHospitals(RECreateHandlingHospitalsRequest request,
            StreamObserver<Empty> responseObserver) {

        // LoginInfo
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());

        // create handling [/other] hospital
        handlingHospitalService.createHandlingHospitals(request, loginInfo);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override public void deleteHandlingHospitals(REDeleteHandlingHospitalsRequest request,
            StreamObserver<Empty> responseObserver) {

        // LoginInfo
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());

        // create handling [/other] hospital
        handlingHospitalService.deleteHandlingHospitals(request, loginInfo);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    /**
     *
     * @param request REInvitePrUsersRequest
     * @param responseObserver StreamObserver
     */
    @Override public void invitePrUsers(REInvitePrUsersRequest request,
                                        StreamObserver<REInvitePrUsersResponse> responseObserver) {

        // LoginInfo
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());

        REInvitePrUsersResponse reInvitePrUsersResponse = invitationService.handleInvitePrUser(request, loginInfo);

        responseObserver.onNext(reInvitePrUsersResponse);
        responseObserver.onCompleted();
    }

    @Override public void inviteUsers(REInviteUsersListRequest request,
            StreamObserver<REInviteUsersResponse> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());

        REInviteUsersResponse response = invitationService.inviteUsers(request, loginInfo);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void sendMail(RESendMailRequest request,
                         StreamObserver<RESendMailResponse> responseObserver) {
        RESendMailResponse response = invitationService.sendMailInvitation(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override public void listProvisionalUsers(REListProvisionalUsersRequest request,
            StreamObserver<REListProvisionalUsersResponse> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        responseObserver.onNext(userService.getUserProvisional(request,loginInfo));
        responseObserver.onCompleted();
    }

    @Override public void downloadProvisionalUsers(REListProvisionalUsersRequest request,
                                                   StreamObserver<REDownloadProvisionalUsersResponse> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        responseObserver.onNext(userService.downloadProvisionalUsers(request,loginInfo));
        responseObserver.onCompleted();
    }

    /**
     * Get List Staff From Request
     */
    @Override
    public void listPrUsers(REListPrUsersRequest request, StreamObserver<REListPrUsersResponse> responseObserver) {
        // LoginInfo
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        UserAuthorityInfo info = new UserAuthorityInfo();
        info.setUserId(loginInfo.getUserId());

        if(!request.getOfficeId().isEmpty()){
            // admin login user
            info.setOfficeId(request.getOfficeId());
        }else{
            // normal login user
            info.setOfficeId(loginInfo.getOfficeId());
        }

        LoginInfo  login = new LoginInfo(info);
        List<REPrUserItem> reStaffItemList = prUserService.getListStaffs(request, login);

        if (!reStaffItemList.isEmpty()) {
            // REListStaffsResponse
            REListPrUsersResponse reListPrUsersResponse = REListPrUsersResponse.newBuilder()
                    .addAllUser(reStaffItemList)
                    .build();

            responseObserver.onNext(reListPrUsersResponse);
        } else {
            responseObserver.onNext(REListPrUsersResponse.getDefaultInstance());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getPrUser(REGetPrUserRequest request, StreamObserver<REPrUser> responseObserver) {

        responseObserver.onNext(prUserService.getPrUser(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getPrUserByOfficeUserIdOrUserId(REGetPrUserByOfficeUserIdOrUserIdRequest request, StreamObserver<REPrUser> responseObserver) {

        responseObserver.onNext(prUserService.getPrUserByOfficeUserIdOrUserId(request));
        responseObserver.onCompleted();
    }

    @Override
    public void listPrUser(REListPrUserByIdsRequest request, StreamObserver<REListPrUser> responseObserver) {
        responseObserver.onNext(prUserService.listPrUser(request));
        responseObserver.onCompleted();
    }

    @Override
    public void putPrUser(REPrUser request, StreamObserver<Empty> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        prUserService.putPrUser(request, loginInfo);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void listBlockUsers(REListBlockUsersRequest request, StreamObserver<REListBlockUsersResponse> responseObserver) {
        responseObserver.onNext(blockedUserService.getListBlockUser(request.getUserId(), request.getOfficeId()));
        responseObserver.onCompleted();
    }

    @Override
    public void listMeetingConfigure(REListMeetingConfigureRequest request, StreamObserver<REListMeetingConfigureResponse> responseObserver) {

        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        List<REMeetingConfigure> reMeetingConfigureList =  meetingConfigureService.listMeetingConfigure(request, loginInfo);
        if (!CollectionUtils.isEmpty(reMeetingConfigureList))
        {
            REListMeetingConfigureResponse reMeetingConfigureResponse = REListMeetingConfigureResponse.newBuilder()
                    .addAllConfigure(reMeetingConfigureList)
                    .build();
            responseObserver.onNext(reMeetingConfigureResponse);
        }
        else {
            responseObserver.onNext(REListMeetingConfigureResponse.getDefaultInstance());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void addUserConnection(REAddListUserConnectionRequest request, StreamObserver<Empty> responseObserver) {
        userService.addUserConnection(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }


    @Override
    public void putIdentifyStatus(REPutIdentifyStatusRequest request, StreamObserver<Empty> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        handlingHospitalService.putIdentifyStatus(request,loginInfo);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void putMeetingRestriction(REPutMeetingRestrictionRequest request, StreamObserver<Empty> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        handlingHospitalService.putMeetingRestriction(request,loginInfo);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }


    @Override
    public void listAsignees(REListAsigneesRequest request, StreamObserver<REListAsigneesResponse> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        responseObserver.onNext(handlingHospitalService.listAssignee(request, loginInfo));
        responseObserver.onCompleted();
    }

    @Override
    public void listMRUserHandleOffice(REListMRUserHandleOfficeRequest request, StreamObserver<REListMRUserHandleOfficeResponse> responseObserver) {
        REListMRUserHandleOfficeResponse response = officeUserService.listMRUserHandleOffice(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listUserByAuthorityAndOfOffice(REListUserSpecifiedAuthorityRequest request, StreamObserver<REListUserSpecifiedAuthorityResponse> responseObserver) {
        REListUserSpecifiedAuthorityResponse response = officeUserService.listUserByAuthorityAndOfOffice(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    @Override
    public void listAsigneesHistory(REListAsigneesHistoryRequest request, StreamObserver<REListAsigneesHistoryResponse> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        responseObserver.onNext(handlingHospitalService.listAssigneeHistory(request, loginInfo));
        responseObserver.onCompleted();
    }

    @Override
    public void listMailUnConfirmAdditional(REListMailUnConfirmAdditionalRequest request, StreamObserver<REListMailUnConfirmAdditionalResponse> responseObserver) {
        REListMailUnConfirmAdditionalResponse response = userService.getListMailUserSettings(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listVisitableUsers(REListVisitableUsersRequest request, StreamObserver<REListVisitableUsersResponse> responseObserver) {
        REListVisitableUsersResponse response = officeUserService.getVisitableUser(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateImageProfile(REUpdateImageProfileRequest request, StreamObserver<Empty> responseObserver) {
        userService.updateImageProfile(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void updateImageProfileOnly(REUpdateImageProfileOnlyRequest request, StreamObserver<Empty> responseObserver) {
        userService.updateImageProfileOnly(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }



    //TODO : confirm customer flow check password of user
    @Override
    public void checkPassword(RECheckPasswordRequest request,
                              StreamObserver<RECheckPasswordResponse> responseObserver) {

        RECheckPasswordResponse response = passwordService.isPassword(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void putMeetingConfigure(REMeetingConfigure reMeetingConfigure,
                              StreamObserver<Empty> responseObserver) {
        meetingConfigureService.putMeetingConfigure(reMeetingConfigure);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getListUser(REUserListRequest request, StreamObserver<REUserListRespone> responseObserver) {
        REUserListRespone.Builder builder = REUserListRespone.newBuilder();

        // get map consist of key is userId and value is officeId
        Map<String, String> userIdAndOfficeIds = new HashMap<>();
        request.getUserRequestList().forEach(user -> {
            userIdAndOfficeIds.put(user.getUserId(), user.getOfficeId());
        });

        Map<String, REUser> results = userService.getListUser(userIdAndOfficeIds);

        if (!CollectionUtils.isEmpty(results)) {
            results.keySet().forEach(userId -> {
                REUserResponse response = REUserResponse.newBuilder().setUserId(userId).setUser(results.get(userId)).build();
                builder.addUserList(response);
            });
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getListUserByOfficeIdAndUserId(REUserListRequest request, StreamObserver<REUserListRespone> responseObserver) {
        // get map consist of key is userId and value is officeId
        Map<String, String> userIdAndOfficeIds = new HashMap<>();
        request.getUserRequestList().forEach(user -> {
            userIdAndOfficeIds.put(user.getUserId(), user.getOfficeId());
        });
        REUserListRespone reUserListRespone = userService.getListUserByOfficeIdAndUserId(userIdAndOfficeIds);
        responseObserver.onNext(reUserListRespone);
        responseObserver.onCompleted();
    }

    @Override
    public void getListHandleUsers(REUserRequest request, StreamObserver<REUserListRespone> responseObserver) {
        REUserListRespone reUserListResponse = userService.getListHandleUsers(request.getUserId(),request.getOfficeId());
        responseObserver.onNext(reUserListResponse);
        responseObserver.onCompleted();
    }
    @Override
    public void forgetPassword(REForgetPasswordRequest request, StreamObserver<Empty> responseObserver) {

        passwordService.forgetPassword(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void resetPassword(REResetPasswordRequest request, StreamObserver<Empty> responseObserver) {

        passwordService.resetPassword(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void updateIdentify(Empty request, StreamObserver<Empty> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        handlingHospitalService.updateIdentify(loginInfo);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void updatePersonalAccount(REPersonalUserRequest request, StreamObserver<Empty> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get();
        personalService.updatePersonalAccount(request,loginInfo);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getPersonalAccount(Empty request, StreamObserver<REPersonalUserResponse> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get();
        REPersonalUserResponse response =  personalService.getPersonalAccount(loginInfo);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void allowDrSeen(REAllowDrSeenRequest request, StreamObserver<REAllowDrSeenResponse> responseObserver) {
        responseObserver.onNext(personalService.prOptionSetting(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getUserList(REGetUserListRequest request, StreamObserver<REListUser> responseObserver) {
        REListUser reponse = userService.getUserList(request);
        responseObserver.onNext(reponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getMRUser(REGetMrUserRequest request, StreamObserver<REGetMrUserResponse> responseObserver) {
        REGetMrUserResponse response = prUserService.getMRUser(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateImageProfileAndIdentificationImage(UpdateImageProfileAndIdentificationImageRequest request, StreamObserver<Empty> responseObserver) {
        userService.updateImageProfileAndIdentificationImage(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();


    }

    @Override public void getUserByOfficeUserId(REGetUserByOfficeUserIdRequest request, StreamObserver<REUser> responseObserver) {

        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        responseObserver.onNext(userService.getUserByOfficeUserId(request ,loginInfo));
        responseObserver.onCompleted();
    }

    @Override public void getUserByOfficeUserIdOrUserId(REGetUserByOfficeUserIdOrUserIdRequest request, StreamObserver<REUser> responseObserver) {

        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        responseObserver.onNext(userService.getUserByOfficeUserIdOrUserId(request ,loginInfo));
        responseObserver.onCompleted();
    }

    @Override
    public void getListOfficeIdHandlingHospital(REGetListOfficeIdHandlingHospitalRequest request, StreamObserver<REGetListOfficeIdHandlingHospitalResponse> responseObserver) {
        responseObserver.onNext(handlingHospitalService.getListOfficeIdHandlingHospital(request));
        responseObserver.onCompleted();
    }

    @Override
    public void listUserByOfficeUserId(REListUserByOfficeUserIdRequest request, StreamObserver<REListUserByOfficeUserIdResponse> responseObserver) {
        responseObserver.onNext(userService.getAllOfficeUserByOfficeUserId(request));
        responseObserver.onCompleted();
    }

    @Override
    public void listOfficeByListOfficeId(REListOfficeByListOfficeIdRequest request, StreamObserver<REListOfficeByListOfficeIdResponse> responseObserver) {
        responseObserver.onNext(officeService.getAllOfficeByOfficeId(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getListUserByCondition(REGetListUserByConditionRequest request, StreamObserver<REGetListUserByConditionResponse> responseObserver) {
        REGetListUserByConditionResponse response = userService.getListUserByCondition(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void getListDrWithCondition(REGetListDrWithConditionRequest request, StreamObserver<REGetListDrWithConditionResponse> responseObserver) {
        LoginInfo loginInfo = GrpcGlobals.LOGIN_INFO.get(Context.current());
        REGetListDrWithConditionResponse reponse = officeUserService.getListDrWithCondition(request, loginInfo);
        responseObserver.onNext(reponse);
        responseObserver.onCompleted();
    }

    @Override
    public void listOfficeIdAndUserId(REOfficeIdAndUserIdRequest request, StreamObserver<REListOfficeIdAndUserIdResponse> responseObserver) {
        responseObserver.onNext(officeUserService.listOfficeIdAndUserId(request.getName()));
        responseObserver.onCompleted();
    }

    @Override
    public void listMedicalOfficeUser(REListMedicalOfficeUserRequest request, StreamObserver<REListMedicalOfficeUserResponse> responseObserver) {
        responseObserver.onNext(mrShareInfoStatusService.listMedicalOfficeUser(request));
        responseObserver.onCompleted();
    }

    @Override
    public void listMrShareInfoStatus(REListMrShareInfoStatusRequest request, StreamObserver<REListMrShareInfoStatusResponse> responseObserver) {
        responseObserver.onNext(mrShareInfoStatusService.listMrShareInfoStatus(request));
        responseObserver.onCompleted();
    }

    @Override
    public void putMrShareInfoStatus(REPutMrShareInfoStatusRequest request, StreamObserver<REPutMrShareInfoStatusResponse> responseObserver) {
        responseObserver.onNext(mrShareInfoStatusService.putMrShareInfoStatus(request));
        responseObserver.onCompleted();
    }

    /*****************************/
    //　認可処理
    @Override
    public void getFunctions(REGetFunctionsRequest request,
            StreamObserver<REGetFunctionsResponse> responseObserver) {
        REGetFunctionsResponse reGetFunctionsResponse = functionService.getFunctions(request);
        responseObserver.onNext(reGetFunctionsResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void saveFunction(RESaveFunctionRequest request,StreamObserver<Empty> responseObserver){
        functionService.saveFunction(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void saveRoles(RESaveRolesRequest request,StreamObserver<Empty> responseObserver){
        roleService.saveRoles(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getRoles(REGetRolesRequest request,
            StreamObserver<REGetRolesResponse> responseObserver) {
        REGetRolesResponse reGetRolesResponse = roleService.getRoles(request);
        responseObserver.onNext(reGetRolesResponse);
        responseObserver.onCompleted();
    }


    @Override
    public void saveOfficeFunction(RESaveOfficeFunctionRequest request,StreamObserver<Empty> responseObserver){
        officeFunctionService.saveOfficeFunction(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    /*****************************/

    @Override
    public void getListOffice(REGetListOfficeRequest request, StreamObserver<REGetListOfficeResponse> responseObserver) {
        REGetListOfficeResponse response = officeService.getListOffice(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getListRoleCode(REGetListRoleCodeRequest request, StreamObserver<REGetListRoleCodeResponse> responseObserver) {
        REGetListRoleCodeResponse response = officeUserRoleService.getListRoleCode(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Get list officeuser by id and office id
     * @param request {@link REGetListOfficeRequest}
     * @param responseObserver {@link REGetListOfficeUserIdsByOfficeIdsResponse}
     */
    @Override
    public void getListOfficeUserIdsByOfficeIds(REGetListOfficeRequest request, StreamObserver<REGetListOfficeUserIdsByOfficeIdsResponse> responseObserver) {
        REGetListOfficeUserIdsByOfficeIdsResponse response = officeService.getListOfficeUserIdsByOfficeIds(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void listHandlingHospitalsNowAndPast(REListHandlingHospitalsNowAndPastRequest request, StreamObserver<REListHandlingHospitalsNowAndPastResponse> responseObserver) {
        REListHandlingHospitalsNowAndPastResponse response = handlingHospitalService.listHandlingHospitalsNowAndPast(request);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getStaffRequestByManagerId(REGetStaffRequestByManagerIdRequest request,
                                           StreamObserver<REGetStaffRequestByManagerIdResponse> responseObserver) {
        responseObserver.onNext(officeUserService.getStaffRequestByManagerId(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getListUserNameByListOfficeUserId(REGetListUserNameByListUserIdRequest request,
                                                  StreamObserver<REGetListUserNameByListUserIdResponse> responseObserver) {
        REGetListUserNameByListUserIdResponse response = userService.getListUserNameByListOfficeUserId(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getListDepartmentIdByOfficeId(REGetListDepartmentIdByOfficeIdRequest request,
                                              StreamObserver<REGetListDepartmentIdByOfficeIdResponse> responseObserver) {
        REGetListDepartmentIdByOfficeIdResponse response = userService.getListDepartmentIdByOfficeId(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getListDepartmentByDepartmentId(REGetListDepartmentByOfficeIdRequest request,
                                                StreamObserver<REGetDepartmentResponse> responseObserver) {
        REGetDepartmentResponse response = departmentService.getListDepartmentByDepartmentId(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    @Override
    public void listOfficeByKeyword(REListOfficeByKeywordRequest request, StreamObserver<REListOfficeByListOfficeIdResponse> responseObserver) {
        responseObserver.onNext(officeService.listOfficeByKeyword(request));
        responseObserver.onCompleted();
    }

    @Override
    public void countUserIsProvisionalAndValid(REListUserByOfficeUserIdRequest request, StreamObserver<RECountUserByListOfficeUserIdResponse> responseObserver) {
        responseObserver.onNext(userService.countUserIsProvisionalAndValid(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getAllMRLinkedUserLoggedInByOfficeUserIds(REGetUserByOfficeUserIdRequest request, StreamObserver<REListUserByOfficeUserIdResponse> responseObserver) {
        responseObserver.onNext(officeUserService.getAllMRLinkedUserLoggedInByOfficeUserIds(request));
        responseObserver.onCompleted();
    }

    @Override
    public void checkHandlingHospital(RECheckHandlingHospitalRequest request, StreamObserver<RECheckHandlingHospitalResponse> responseObserver) {
        responseObserver.onNext(handlingHospitalService.checkHandlingHospital(request));
        responseObserver.onCompleted();
    }

    @Override
    public void checkHandlingHospitalForListGroup(RECheckHandlingHospitalForListGroupRequest request, StreamObserver<RECheckHandlingHospitalForListGroupResponse> responseObserver) {
        responseObserver.onNext(handlingHospitalService.checkHandlingHospitalForListGroup(request));
        responseObserver.onCompleted();
    }

    @Override
    public void removeMrAdoptedDrugs(RERemoveMrAdoptedDrugsRequest request, StreamObserver<Empty> responseObserver) {
        officeUserService.removeMrAdoptedDrugs(request);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getListUserLevelLimitedByCondition(REGetListUserByConditionRequest request,
                                                   StreamObserver<REGetListUserByConditionResponse> responseObserver) {
        REGetListUserByConditionResponse response = userService.getListUserLevelLimitedByCondition(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void getListUserByRequestManagementAuthorityCondition(
            REGetListUserByRequestManagementAuthorityConditionRequest request,
            StreamObserver<REGetListUserByConditionResponse> responseObserver) {
        REGetListUserByConditionResponse response = userService.getListUserByRequestManagementAuthorityCondition(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void getListManagerRequest(REGetListManagerRequestRequest request,
                                      StreamObserver<REGetListManagerRequestResponse> responseObserver) {
        responseObserver.onNext(userService.getListManagerRequest(request));
        responseObserver.onCompleted();
    }


    @Override
    public void getStaffRequestItem(REGetStaffRequestItemRequest request,
                                    StreamObserver<REGetStaffRequestItemResponse> responseObserver) {
        responseObserver.onNext(officeUserService.getStaffRequestItem(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getListUserByOfficeId(REGetListUserByOfficeIdRequest request,
                                      StreamObserver<REGetListUserByOfficeIdResponse> responseObserver) {
        responseObserver.onNext(officeUserService.getListUserByOfficeIdAndManagerRequestId(request));
        responseObserver.onCompleted();
    }


    @Override
    public void getListOfficeUserByOfficeId(REListOfficeUserByOfficeIdRequest request,
                                            StreamObserver<REListOfficeUserByOfficeIdResponse> responseObserver) {
        responseObserver.onNext(officeUserService.getListOfficeUserByOfficeId(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getListDepartmentByOfficeId(REGetListDepartmentByOfficeIdRequest request,
                                            StreamObserver<REGetListDepartmentByOfficeIdResponse> responseObserver) {
        REGetListDepartmentByOfficeIdResponse response = userService.getListDepartmentByOfficeId(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getListDepartmentChildrenById(REGetListDepartmentChildrenByIdRequest request,
                                              StreamObserver<REGetListDepartmentChildrenByIdRequestResponse> responseObserver) {

        REGetListDepartmentChildrenByIdRequestResponse response = departmentService.getListDepartmentChildrenById(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * 事業所名を取得する
     */
    @Override
    public void getOfficeNamesByOfficeUserIds(
            REGetOfficeNamesByOfficeUserIdsRequest request,
            StreamObserver<REGetOfficeNamesByOfficeUserIdsResponse> responseObserver) {
        final Map<String, String> officeUserIdToOfficeNameMap =
                officeService.getOfficeNamesByOfficeUserId(request.getOfficeUserIdList());
        final REGetOfficeNamesByOfficeUserIdsResponse response =
                REGetOfficeNamesByOfficeUserIdsResponse.newBuilder()
                        .putAllOfficeUserIdToOfficeNameMap(officeUserIdToOfficeNameMap)
                        .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getBuildingAndConferenceRoomSetting(
            REGetBuildingAndConferenceRoomSettingRequest request, StreamObserver<REConferenceRoom> responseObserver) {
        REConferenceRoom response = conferenceRoomService.getBuildingAndConferenceRoomSetting(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getBuildingsAndConferenceRoomsSetting(REGetBuildingsAndConferenceRoomsSettingRequest request, StreamObserver<REGetBuildingsAndConferenceRoomsSettingResponse> responseObserver){
        REGetBuildingsAndConferenceRoomsSettingResponse response = conferenceRoomService.getBuildingsAndConferenceRoomsSetting(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void saveBuildingsAndConferenceRoomsSetting(RESaveBuildingsAndConferenceRoomsSettingRequest request, StreamObserver<RESaveBuildingsAndConferenceRoomsSettingResponse> responseObserver){
        RESaveBuildingsAndConferenceRoomsSettingResponse response = conferenceRoomService.saveBuildingsAndConferenceRoomsSetting(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findAvailableConferenceRooms(REFindAvailableConferenceRoomsRequest request, StreamObserver<REFindAvailableConferenceRoomsResponse> responseObserver) {
        REFindAvailableConferenceRoomsResponse response = conferenceRoomService.findAvailableConferenceRooms(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void getListOfficeUserIdByIndustry(REListOfficeUserIdByIndustryRequest request,
            StreamObserver<REListOfficeUserIdByIndustryResponse> responseObserver) {

        REListOfficeUserIdByIndustryResponse response = officeUserService.getListOfficeUserIdByIndustry(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getMROfficeUserIdByName(REGetMROfficeUserIdByNameRequest request,
                                        StreamObserver<REGetMROfficeUserIdByNameResponse> responseObserver) {

        REGetMROfficeUserIdByNameResponse response = officeUserService.getMROfficeUserIdByName(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserIdListByOfficeId(REGetUserIdListByOfficeIdRequest request, StreamObserver<REGetUserIdListByOfficeIdResponse> responseObserver) {
        REGetUserIdListByOfficeIdResponse response = userService.getUserIdListByOfficeId(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getListOfficeByName(REListOfficeByNameRequest request,
                                    StreamObserver<REListOfficeByNameResponse> responseObserver) {
        REListOfficeByNameResponse response = officeService.getListOfficeByName(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getListMrByOfficeId(REGetListMrByOfficeIdRequest request, StreamObserver<REGetListMrByOfficeIdResponse> responseObserver) {
        List<REPrUserItem> reStaffItemList = prUserService.getListMrByOfficeId(request);
        if (!reStaffItemList.isEmpty()) {
            // REListStaffsResponse
            REGetListMrByOfficeIdResponse reListPrUsersResponse = REGetListMrByOfficeIdResponse.newBuilder()
                    .addAllUser(reStaffItemList)
                    .build();

            responseObserver.onNext(reListPrUsersResponse);
        } else {
            responseObserver.onNext(REGetListMrByOfficeIdResponse.getDefaultInstance());
        }
        responseObserver.onCompleted();
    }

    /**
     * CH0012: Get all MR in Pharmacy
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void getMrOfficeUserIdsByOfficeId(REGetMrOfficeUserIdsByOfficeIdRequest request, StreamObserver<REGetMrOfficeUserIdsByOfficeIdResponse> responseObserver) {
        REGetMrOfficeUserIdsByOfficeIdResponse response = prUserService.getMrOfficeUserIdsByOfficeId(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * CR: 10930 Get Pharmacy office
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void getPrOfficesByDrOfficeId(REPrOfficesByDrOfficeIdRequest request,
                                         StreamObserver<REPrOfficesByDrOfficeIdResponse> responseObserver) {
        List<REPharmacyOfficeInfo> listPrOffice = handlingHospitalService.listPrHandlingHospitals(request);
        if (!CollectionUtils.isEmpty(listPrOffice)) {
            REPrOfficesByDrOfficeIdResponse response = REPrOfficesByDrOfficeIdResponse.newBuilder()
                    .addAllPrOfficeInfo(listPrOffice)
                    .build();

            responseObserver.onNext(response);
        } else {
            responseObserver.onNext(REPrOfficesByDrOfficeIdResponse.getDefaultInstance());
        }
        responseObserver.onCompleted();
    }
    @Override
    public void getListOfficeUserByCondition(REGetListOfficeUserByConditionRequest request, StreamObserver<REGetListOfficeUserByConditionResponse> responseObserver) {
        responseObserver.onNext(userService.getListOfficeUserByCondition(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getOfficeUsersByOfficeId(REGetOfficeUsersByOfficeIdRequest request, StreamObserver<REGetOfficeUsersByOfficeIdResponse> responseObserver) {
        responseObserver.onNext(userService.getOfficeUsersByOfficeId(request));
        responseObserver.onCompleted();
    }
    //PhucLQ
    @Override
    public void getAddStudent(SaveStudentRequest request, StreamObserver<ListStudentResponse> responseObserver) {
        responseObserver.onNext(userService.addStudent(request));
        responseObserver.onCompleted();
    }
    @Override
    public void getEditStudent(EditStudentRequest request, StreamObserver<ListStudentResponse> responseObserver) {
        responseObserver.onNext(userService.editStudent(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getDelStudent(DelStudentRequest request, StreamObserver<Empty> responseObserver) {
        userService.delStudent(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getSortByNameStudent(Empty request, StreamObserver<ListStudentResponse> responseObserver) {
        responseObserver.onNext(userService.sortByNameStudent(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getSortByGpaStudent(Empty request, StreamObserver<ListStudentResponse> responseObserver) {
        responseObserver.onNext(userService.sortByGpaStudent(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getShowStudent(Empty request, StreamObserver<ListStudentResponse> responseObserver) {
        responseObserver.onNext(userService.showStudent(request));
        responseObserver.onCompleted();
    }
}
