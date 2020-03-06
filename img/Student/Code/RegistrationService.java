package jp.drjoy.service.web.api.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Empty;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import jp.drjoy.backend.registration.domain.model.Student;
import jp.drjoy.core.autogen.grpc.common.CMNPage;
import jp.drjoy.core.autogen.grpc.common.CMNSort;
import jp.drjoy.core.autogen.grpc.registration.*;
import jp.drjoy.core.autogen.grpc.rtm.RTCheckMultiExistedRoomResponse;
import jp.drjoy.service.framework.model.Product;
import jp.drjoy.service.framework.security.Authoritys;
import jp.drjoy.service.framework.security.model.LoginInfo;
import jp.drjoy.service.framework.utils.Strings;
import jp.drjoy.service.web.api.service.attendance.AttendanceStubService;
import jp.drjoy.service.web.model.Company;
import jp.drjoy.service.web.model.DepartmentSettings;
import jp.drjoy.service.web.model.JobType;
import jp.drjoy.service.web.model.OfficeSetting;
import jp.drjoy.service.web.model.OfficeUser;
import jp.drjoy.service.web.model.OfficeUserCondition;
import jp.drjoy.service.web.model.Password;
import jp.drjoy.service.web.model.PrFirstEntryInitData;
import jp.drjoy.service.web.model.PrHandlingHospitals;
import jp.drjoy.service.web.model.PrHospitalAssociate;
import jp.drjoy.service.web.model.PrKeyCode;
import jp.drjoy.service.web.model.PrLoginLogout;
import jp.drjoy.service.web.model.PrPassword;
import jp.drjoy.service.web.model.PrReNotificationSettings;
import jp.drjoy.service.web.model.PrStaffListInitData;
import jp.drjoy.service.web.model.PrUserEdit;
import jp.drjoy.service.web.model.PrUserEditInitData;
import jp.drjoy.service.web.model.PrUserItem;
import jp.drjoy.service.web.model.PrepareDepartmentUser;
import jp.drjoy.service.web.model.PrepareOutsideOfficeUser;
import jp.drjoy.service.web.model.Profile;
import jp.drjoy.service.web.model.ReNotificationSettings;
import jp.drjoy.service.web.model.ReSideMenuSettings;
import jp.drjoy.service.web.model.SpecialtyArea;
import jp.drjoy.service.web.model.StaffEdit;
import jp.drjoy.service.web.model.StaffEditInitData;
import jp.drjoy.service.web.model.StaffInvite;
import jp.drjoy.service.web.model.StaffInviteInitData;
import jp.drjoy.service.web.model.StaffListInitData;
import jp.drjoy.service.web.model.User;
import jp.drjoy.service.web.model.UserEdit;
import jp.drjoy.service.web.model.UserEditInitData;
import jp.drjoy.service.web.model.UserFirstEntry;
import jp.drjoy.service.web.model.UserSession;
import jp.drjoy.service.web.model.UsersResponse;
import jp.drjoy.service.web.request.meeting.PinStaffRequest;
import jp.drjoy.service.web.request.registration.DeleteListUserRequest;
import jp.drjoy.service.web.request.registration.GetPrStaffListRequest;
import jp.drjoy.service.web.request.registration.Page;
import jp.drjoy.service.web.request.registration.PrCreateUserRequest;
import jp.drjoy.service.web.request.registration.PrInviteUsersRequest;
import jp.drjoy.service.web.request.registration.ResetPasswordRequest;
import jp.drjoy.service.web.request.registration.SaveBuildingsAndConferenceRoomsRequest;
import jp.drjoy.service.web.request.registration.Sort;
import jp.drjoy.service.web.request.registration.UsersRequest;
import jp.drjoy.service.web.request.webmeeting.PutSettingDrRequest;
import jp.drjoy.service.web.response.chat.GetPrepareCreateInsideRoomResponse;
import jp.drjoy.service.web.response.chat.GetPrepareCreateOutsideRoomResponse;
import jp.drjoy.service.web.response.group.GetPrepareCreateInsideDepartmentGroupResponse;
import jp.drjoy.service.web.response.group.GetPrepareCreateInsideGroupResponse;
import jp.drjoy.service.web.response.group.OutsideOfficeUserRespone;
import jp.drjoy.service.web.response.group.PrepareCreateOutsideGroupResponse;
import jp.drjoy.service.web.response.meeting.GetAllPICResponse;
import jp.drjoy.service.web.response.meeting.GetListHandleUsersResponse;
import jp.drjoy.service.web.response.meeting.GetListStaffResponse;
import jp.drjoy.service.web.response.meeting.GetPICResponse;
import jp.drjoy.service.web.response.meeting.GetStaffListResponse;
import jp.drjoy.service.web.response.meeting.GetVisitableUsersResponse;
import jp.drjoy.service.web.response.pharmacy.GetDrugStoreByEmailResponse;
import jp.drjoy.service.web.response.reaction_reporting.ListOfficeResponse;
import jp.drjoy.service.web.response.registration.GetBuildingsAndConferenceRoomsResponse;
import jp.drjoy.service.web.response.registration.GetHandlingHospitalResponse;
import jp.drjoy.service.web.response.registration.GetPharmacyOfficeResponse;
import jp.drjoy.service.web.response.registration.Office;
import jp.drjoy.service.web.response.registration.PrUsersResponse;
import jp.drjoy.service.web.response.registration.RestrictedOfUserResponse;
import jp.drjoy.service.web.utils.CommonUtils;
import jp.drjoy.spring.boot.grpc.client.GrpcClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static jp.drjoy.service.web.utils.AttendanceConstants.DEFAULT_DEPARTMENT_FOR_ALL;
import static jp.drjoy.service.web.utils.AttendanceConstants.DEFAULT_JOB_TYPE_FOR_ALL;

@Service
public class RegistrationService {

    private static Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    private static final String TOKEN = ",";
    public static final boolean GET_NAME_ABBREVIATION = true;
    public static final String SORT_BY_DEFAULT_LIST_MR = "NAME";
    public static final String SORT_BY_LAST_NAME_KANA = "lastNameKana";

    @GrpcClient(value = "registration")
    private Channel registrationChannel;

    @Autowired private MasterService masterService;
    @Autowired private RtmService rtmService;
    @Autowired private ReactionReportingService reactionReportingService;
    @Autowired private AttendanceStubService attendanceStubService;

    // Constructors
    // ------------------------------------------------------------------------
    public RegistrationService() {
    }

    @VisibleForTesting
    protected RegistrationService(final Channel registrationChannel) {
        this.registrationChannel = registrationChannel;
    }

    @VisibleForTesting
    protected RegistrationService(final Channel registrationChannel, ReactionReportingService reactionReportingService) {
        this.registrationChannel = registrationChannel;
        this.reactionReportingService = reactionReportingService;
    }

    private LoginInfo getLoginInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        return (LoginInfo) context.getAuthentication().getPrincipal();
    }

    /**
     * ユーザーセッション情報を取得します.
     */
    public UserSession getUserSession() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (!(context.getAuthentication().getPrincipal() instanceof LoginInfo)) {
            return new UserSession();
        }
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REUserSessionResponse res = stub.getUserSession(Empty.getDefaultInstance());
        return new UserSession(res);
    }

    /**
     * 事業所ユーザの一覧
     */
    public List<OfficeUser> listOfficeUsers(
        String officeId, String departmentId, String keyword, Sort sort, Page page, boolean detail
    ) {
        if (officeId.equals("self")) {
            officeId = getLoginInfo().getOfficeId();
        }
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        // List時は、MongoDBへのアクセスを減らすためDBRefを解決しない
        ListOfficeUsersRequest request = ListOfficeUsersRequest.newBuilder()
            .setOfficeId(officeId).setDepartmentId(departmentId).setKeyword(keyword)
            .setSort(sort.toMessage()).setPage(page.toMessage()).setOmitDBRefs(true).build();
        ListOfficeUsersResponse response = stub.listOfficeUsers(request);
        List<OfficeUser> officeUsers = response.getOfficeUsersList().stream()
            .map(m -> OfficeUser.create(m, !detail)).collect(Collectors.toList());
        // officeIdもしくはdepartmentIdが指定された場合、officeIdは自明なので省略する
        if (!officeId.isEmpty() || !departmentId.isEmpty()) {
            officeUsers.forEach(i -> i.setOfficeId(null));
        }
        return officeUsers;
    }

    /**
     * 事業所ユーザの取得
     */
    public OfficeUser getOfficeUser(String id, boolean detail) {
        String loginOfficeUserId = getLoginInfo().getOfficeUserId();
        if (id.equals("self")) {
            id = loginOfficeUserId;
        }
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        OfficeUserMessage response = stub.getOfficeUser(
            jp.drjoy.core.autogen.grpc.registration.GetOfficeUserRequest.newBuilder()
                .setId(id).setOmitDBRefs(!detail).build()
        );
        OfficeUser officeUser = OfficeUser.create(response, !detail);
        // 他人の場合、設定情報は不要そうなので省略する (他にもあればここで)
        if (!id.equals(loginOfficeUserId)) {
            officeUser.setSettings(null);
        }
        return officeUser;
    }

    /**
     * 事業所の一覧
     */
    public List<Office> listOffices(boolean detail) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.listOffices(ListOfficesRequest.newBuilder().setDetail(detail).build())
            .getOfficesList().stream().map(m -> Office.createFrom(m)).collect(Collectors.toList());
    }

    /**
     * 事業所の取得
     */
    public Office getOffice(String id, boolean detail) {
        if (id.equals("self")) {
            id = getLoginInfo().getOfficeId();
        }
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return Office.createFrom(stub.getOffice(
            GetOfficeRequest.newBuilder().setId(id).setDetail(detail).build()));
    }

    /**
     * RE0021 通知設定のロード
     *
     * @return 通知設定
     */
    public ReNotificationSettings getNotificationSettings() {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        RENotificationSettings settings = stub.getNotificationSettings(Empty.getDefaultInstance());
        return new ReNotificationSettings(settings);
    }

    /**
     * RE0021 通知設定の保存
     *
     * @param settings 通知設定
     */
    public void saveNotificationSettings(ReNotificationSettings settings) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.putNotificationSettings(settings.asNotificationSettings());
    }

    /**
     * RE0019 パスワード変更
     *
     * @param password パスワード
     */
    public void changePassword(Password password) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc
            .newBlockingStub(registrationChannel);

        stub.updatePassword(REUpdatePasswordRequest.newBuilder()
            .setCurrentPassword(password.getCurrentPassword())
            .setNewPassword(password.getNewPassword())
            .build());
    }

    /**
     * RE0001 サイドメニュー設定取得
     */
    public ReSideMenuSettings getSideMenu() {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        RESideMenuSettings settings = stub.getSideMenuSettings(Empty.getDefaultInstance());
        return new ReSideMenuSettings(settings);
    }

    /**
     * RE0001 サイドメニュー設定保存
     */
    public void putSideMenu(ReSideMenuSettings settings) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.putSideMenuSettings(settings.asSideMenuSettings());
    }

    /**
     * RE0004 Keyコード登録
     */

    public void registKeyCode(PrKeyCode keyCode) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc
            .newBlockingStub(registrationChannel);
        RERegisterKeyCodeRequest request = RERegisterKeyCodeRequest.newBuilder()
            .setMailAddress(keyCode.getMailAddress())
            .setKeycode(keyCode.getKeyCode())
            .build();
        stub.registerKeyCode(request);
    }

    /**
     * RE0006　会社名検索
     */
    public List<Company> searchCompanyName(String companyName) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0006　初期データ取得
     */
    public PrFirstEntryInitData firstEntryGetIntilData(PrFirstEntryInitData prFirstEntryInitData) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0006　初回登録
     */
    public void registFirstEntry(PrCreateUserRequest prCreateUserRequest) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0008 病院を紐づけ登録
     */
    public void hospitalAssociateRegist(PrHospitalAssociate prHospitalAssociate) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0009 パスワード変更
     */
    public void prChangePassword(PrPassword password) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc
            .newBlockingStub(registrationChannel);

        REUpdatePasswordRequest request = REUpdatePasswordRequest.newBuilder()
            .setCurrentPassword(password.getCurrentPassword())
            .setNewPassword(password.getNewPassword())
            .build();
        stub.updatePassword(request);
    }

    /**
     * RE0010 ユーザー情報取得
     */
    public PrUserEditInitData prUserEditInitData(PrUserEditInitData prUserEditInitData) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0010 メール追加
     */
    public void prUserEditAddMailAddress(String emailAddress) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0010 ユーザー情報変種
     */
    public void prUserEditChangeLoginID(String loginID) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0010 通知先・メール変更
     */
    public void prUserEditChangeNotifiMailAddress(String notifiMail) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0010 ユーザー情報編集
     */
    public void userEdit(PrUserEdit prUserEdit) {
        throw new RuntimeException("not implemented");
    }

    /**
     * notifi
     * RE0010 通知メール追加
     */
    public void prUserEditAddNotifiMailAddress(String NotifiemailAddress) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0010 有効期限確認
     */
    public boolean checkPermission(String password) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0011 通知設定取得
     *
     * @return 通知設定
     */
    public PrReNotificationSettings getPrNotificationSettings() {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        RENotificationSettings settings = stub.getNotificationSettings(Empty.getDefaultInstance());
        return new PrReNotificationSettings(settings);
    }

    /**
     * RE0011 通知設定登録
     *
     * @param settings 通知設定
     */
    public void savePrNotificationSettings(PrReNotificationSettings settings) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.putNotificationSettings(settings.asNotificationSettings());
    }

    /**
     * RE0012 担当病院取得
     */
    public PrHandlingHospitals handlingHospitalgetInitData(
        PrHandlingHospitals prHandlingHospitals) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0012 担当病院追加
     */
    public void handlingHospitalsRegist(PrHandlingHospitals prHandlingHospitals) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0012 担当病院削除
     */
    public void handlingHospitaldelete(String handlingHospitalID) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0012 病院検索
     */
    public void handlingHospitalsearch(String handlingHospitalName) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0013 ログインログアウト
     */
    public void loginLogout(PrLoginLogout prLoginLogout) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0014 RE0018 request
     */
    public UsersResponse getUsersRequest(UsersRequest users) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        REListUsersResponse response = stub.listUsers(users.asListUserRequest());

        Set<String> officeUserIds = response.getUserList().stream().map(REUserItem::getOfficeUserId)
            .collect(Collectors.toSet());

        return new UsersResponse(response);
    }

    /**
     * RE0014 download list staff
     * @param departmentId
     * @param name
     * @param accountStatuses
     */
    public UsersResponse downloadStaff(String departmentId, String name, int accountStatuses) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);

        CMNSort.CMNDirection direction = CMNSort.CMNDirection.DESC;

        CMNSort cmnSort = CMNSort.newBuilder()
            .setDirection(direction)
            .setName(SORT_BY_LAST_NAME_KANA)
            .build();

        REListUsersResponse response = stub.listUsers(
            REListUsersRequest.newBuilder()
                .setDepartmentId(Strings.nvl(departmentId))
                .setName(Strings.nvl(name))
                .setAccountStatuses(accountStatuses)
                .setSort(cmnSort)
                .build());
        return new UsersResponse(response);
    }

    /**
     * RE0014 RE0018 response
     */

    public void saveListUserReponse(UsersResponse users) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
    }

    /**
     * RE0014 1Top 情報詳細取得
     */
    public StaffEdit staffAdminViewDetail(String staffID) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0015 情報取得
     */
    public StaffEdit staffInviteGetInitData(StaffInviteInitData staffInviteInitData) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0015 スタッフ一括招待
     */
    public StaffEdit staffInviteTempRegist(StaffInvite staffInvite) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0015 名称検索
     */
    public List<StaffEdit> staffInviteSearch(String staffName) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0015 メール送信
     */
    public void staffInviteSendEmail(String staffID) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0015 CSVダウンロード
     */
    public String staffInviteCSVDownload() {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0016 スタッフ情報詳細取得
     */
    public StaffEditInitData staffEditGetInitData(StaffEditInitData staffEditInitData) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0016 アカウントロック解除
     */
    public void staffEditUnblock(StaffEdit staffEdit) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0016 アカウント削除
     */
    public void staffEditDelete(StaffEdit staffEdit) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0016 パスワードレセット
     */
    public void staffEditResetPassword(StaffEdit staffEdit) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0016 スタッフ情報保存
     */
    public void staffEditSaveUserEdit(StaffEdit staffEdit) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0017 所属情報取得
     */
    public List<DepartmentSettings> getDepartmentSettings() {

        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        REGetDepartmentResponse response = stub.getDepartments(Empty.getDefaultInstance());
        return DepartmentSettings.toDepartmentSettingsList(response);
    }

    /**
     * RE0017 所属情報保存
     */
    public void putDepartmentSettings(List<DepartmentSettings> settings) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.putDepartments(DepartmentSettings.asPutDepartmentRequest(settings));
    }

    /**
     * RE0018 スタッフ情報取得
     */
    public StaffListInitData staffListGetInitData(StaffListInitData staffListInitData) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0018 スタッフ情報保存
     */
    public UserEdit staffListViewDetail(UserEdit userEdit) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0020 ユーザー情報取得
     */
    public void userEditInitData(UserEditInitData userEditInitData) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0020 アカウント削除
     */
    public void userEditDelete(String userEditID) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0020 通知先追加
     */
    public void userEditAddNotifiMailAddress(String notifiMailAddress) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0020 ログインID変更
     */
    public void userEditChangeLoginID(String loginID) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0020 通知先メール変更
     */
    public void userEditChangeNotifiMailAddress(String notifiMailAddress) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0020 ユーザー情報登録
     */
    public void userEdit(UserEdit userEdit) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0020 メールアドレス追加
     */
    public void userEditAddMailAddress(String mailAddress) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0020　有効権限確認
     */
    public void userEditCheckPermission(String password) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0021 ユーザー情報取得
     */
    public void notificationGetInitData(RENotificationSettings settings) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0021　有効権限確認
     */
    public void notificationEdit(RENotificationSettings settings) {
        throw new RuntimeException("not implemented");
    }

    /**
     * ユーザ情報を取得する。
     *
     * @param userId ユーザID
     * @param officeId 事業所ID
     */
    public REUser getUser(String userId, String officeId) {
        REGetUserRequest request = REGetUserRequest.newBuilder()
            .setUserId(userId).setOfficeId(officeId).build();
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        REUser reUser = stub.getUser(request);


        return reUser;
    }


    /**
     * ユーザ情報を取得する。
     *
     * @param officeUserId
     */
    public REUser getUserInfo(String officeUserId) {
        List<String> officeUserList = new ArrayList<>();

        officeUserList.add(officeUserId);
        GetUserIdFromListRequest request = GetUserIdFromListRequest.newBuilder().addAllOfficeUserids(officeUserList).build();
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        GetUserIdFromListResponse response = stub.getUserIdFromList(request);

        return getUser(response.getOfficeUserList().get(0).getUserId(),response.getOfficeUserList().get(0).getOfficeId());
    }


    /**
     * ユーザアカウントのロックを解除する。
     *
     * @param userId ユーザID
     * @param officeId 事業所ID
     */
    public void unlockUser(String userId, String officeId) {
        REUnlockUserRequest request = REUnlockUserRequest.newBuilder()
            .setUserId(userId).setOfficeId(officeId).build();
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.unlockUser(request);
    }

    /**
     * ユーザアカウントのロックする。
     *
     * @param userId ユーザID
     * @param officeId 事業所ID
     */
    public void lockUser(String userId, String officeId) {
        RELockUserRequest request = RELockUserRequest.newBuilder()
            .setUserId(userId).setOfficeId(officeId).build();
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.lockUser(request);
    }

    /**
     * ユーザアカウントを削除する。
     *
     * @param userId ユーザID
     */
    public void deleteUser(String userId) {
        REDeleteUserRequest request = REDeleteUserRequest.newBuilder()
            .setUserId(userId).build();
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.deleteUser(request);
    }

    public void deleteListUser(DeleteListUserRequest deleteListUserRequest) {
        REDeleteListUserRequest request = REDeleteListUserRequest.newBuilder()
            .addAllUserIds(deleteListUserRequest.getListUserId()).build();
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.deleteListUser(request);
    }

    /**
     * ユーザ情報を登録する
     *
     * @param user ユーザ情報
     */
    public void putUserRoleRequest(REUser user) {
        LoginInfo loginInfo = Authoritys.getLoginInfo();
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        REUserResponse reUserResponse = stub.putUser(user);

        if (!user.getFuncAuthority().getFP16()) {
            attendanceStubService.updateManagerRequestAuthority(loginInfo.getOfficeId(), reUserResponse.getOfficeUserId());
        }
    }

    public void putUser(REUser user) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.putUser(user);
    }

    /**
     * RE0025　初回登録・確認
     */
    public void firstEntryConfirm(UserFirstEntry userFirstEntry) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0026 招待可能な医療機関一覧取得
     */
    public StaffInviteInitData prStaffInviteGetInitData(StaffInviteInitData staffInviteInitData) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0026 pr社員招待
     */
    public void prStaffInvite(PrInviteUsersRequest prInviteUsersRequest) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0027 スタッフ一覧　データ取得
     */
    public void prstaffListgetInitData(PrStaffListInitData prStaffListInitData) {
        throw new RuntimeException("not implemented");
    }

    /**
     * RE0027 スタッフ一覧 詳細取得
     */
    public void prstaffListGetDetailStaff(String staffID) {
        throw new RuntimeException("not implemented");
    }

    /**
     * 登録情報取得処理
     * @param request get user entry request
     * @return REGetUseEntryResponse
     */
    public REGetUseEntryResponse getUserEntry(REGetUserEntryRequest request) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getUserEntry(request);
    }

    /**
     * ユーザ登録処理
     * @param request {@code RECreateUserRequest}
     */
    public RECreateUserResponse createPrUser(RECreateUserRequest request) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);

        RECreateUserResponse response = stub.createUser(request);
        try {
            reactionReportingService.updateUnseenSideMenuForPr(Collections.singletonList(response.getOfficeUserId()));
        } catch (Exception ex) {
            logger.error("Update side menu RR fail with message: ", ex);
        }

        return response;
    }

    /**
     * 通知メールアドレス変更予約情報取得処理
     * @param request {@code REGetMailAddressChangeReservationRequest}
     * @return {@code REGetMailAddressChangeReservationResponse}
     */
    public REGetMailAddressChangeReservationResponse getMailAddressChangeReservation(
        REGetMailAddressChangeReservationRequest request) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getMailAddressChangeReservation(request);
    }

    /**
     * 通知メールアドレス変更
     * @param request {@code REGetMailAddressChangeReservationRequest}
     */
    public REUpdateMailAddressResponse updateMailAddress(
        REUpdateMailAddressRequest request) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.updateMailAddress(request);
    }

    /**
     * 通知メールアドレス変更予約情報取得処理
     * @param request {@code REGetMailAddressChangeReservationRequest}
     * @return {@code REGetMailAddressChangeReservationResponse}
     */
    public REGetAdditionalMailAddressChangeReservationResponse getAddtionalMailAddressChangeReservation(
        REGetAdditionalMailAddressChangeReservationRequest request) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getAdditionalMailAddressChangeReservation(request);
    }

    /**
     * 通知メールアドレス変更
     * @param request {@code REUpdateAdditionalMailAddressRequest}
     */
    public REUpdateMailAddressResponse updateAdditionalMailAddress(
        REUpdateAdditionalMailAddressRequest request) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.updateAdditionalMailAddress(request);
    }

    /**
     * LuongHH
     *
     * @param staffRequest GetPrStaffListRequest
     * @return {@code PrUsersResponse}
     */
    public PrUsersResponse getPrStaffList(GetPrStaffListRequest staffRequest) {
        LoginInfo loginInfo = Authoritys.getLoginInfo();
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);

        REGetListRoleCodeRequest.Builder reGetListRoleCodeRequest = REGetListRoleCodeRequest.newBuilder();
        reGetListRoleCodeRequest.setOfficeUserId(Strings.nvl(loginInfo.getOfficeUserId()));
        REGetListRoleCodeResponse reGetListRoleCodeResponse = stub.getListRoleCode(reGetListRoleCodeRequest.build());
        List<String> roleCodesList = reGetListRoleCodeResponse.getRoleCodesList();

        logger.info("roleCodesList==:{}", roleCodesList);
        if(roleCodesList.contains(jp.drjoy.service.framework.model.Role.PR_MGT.getCode())) {
            REListPrUsersResponse response = stub.listPrUsers(staffRequest.asListStaffRequest());
            Set<String> officeUserIds = response.getUserList().stream().map(REPrUserItem::getOfficeUserId)
                .collect(Collectors.toSet());

            return new PrUsersResponse(response);
        } else {
            return new PrUsersResponse();
        }
    }

    /**
     * 担当病院取得
     * @return {@code REListHandlingHospitalsResponse}
     */
    public List<GetHandlingHospitalResponse> getHandlingHospitals(String mrUserId, String mrOfficeId) {
        List<GetHandlingHospitalResponse> responseList = new ArrayList<>();
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        REListHandlingHospitalsRequest request = REListHandlingHospitalsRequest.newBuilder()
            .setUserId(mrUserId).setOfficeId(mrOfficeId).build();
        REListHandlingHospitalsResponse response = stub.listHandlingHospitals(request);

        List<REMedicalOffice> handlingHospitalsList = response.getHandlingHostpitalsList();
        for (REMedicalOffice reMedicalOffice: handlingHospitalsList) {
            responseList.add(new GetHandlingHospitalResponse((reMedicalOffice)));
        }

        return responseList;
    }

    /**
     * Get list handling hospital with history
     * @return {@code REListHandlingHospitalsResponse}
     */
    public List<GetHandlingHospitalResponse> getHandlingHospitalsWithHistory(boolean histories, boolean mrShareInfo, String mrUserId, String mrOfficeId) {
        List<GetHandlingHospitalResponse> responseList = new ArrayList<>();
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        REListHandlingHospitalsWithHistoryRequest request = REListHandlingHospitalsWithHistoryRequest.newBuilder()
            .setHistories(histories)
            .setMrShareInfo(mrShareInfo)
            .setUserId(Strings.nvl(mrUserId))
            .setOfficeId(Strings.nvl(mrOfficeId))
            .build();
        REListHandlingHospitalsResponse response = stub.listHandlingHospitalsWithHistory(request);

        List<REMedicalOffice> handlingHospitalsList = response.getHandlingHostpitalsList();
        for (REMedicalOffice reMedicalOffice: handlingHospitalsList) {
            responseList.add(new GetHandlingHospitalResponse((reMedicalOffice)));
        }

        return responseList;
    }

    /**
     * 担当病院取得
     * @return {@code REListHandlingHospitalsResponse}
     */
    public List<GetHandlingHospitalResponse> listHandlingHospitals(boolean notOnlyOtherHospital) {
        List<GetHandlingHospitalResponse> responseList = new ArrayList<>();
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        SecurityContext context = SecurityContextHolder.getContext();
        LoginInfo loginInfo = (LoginInfo) context.getAuthentication().getPrincipal();
        REListHandlingHospitalsRequest request = REListHandlingHospitalsRequest.newBuilder()
            .setGetNameAbbreviation(GET_NAME_ABBREVIATION)
            .setUserId(loginInfo.getUserId())
            .setOtherHospital(notOnlyOtherHospital)
            .setOfficeId(loginInfo.getOfficeId())
            .setHasMeetingRequestSetting(true)
            .build();
        REListHandlingHospitalsResponse response = stub.listHandlingHospitals(request);

        List<REMedicalOffice> handlingHospitalsList = response.getHandlingHostpitalsList();

        for (REMedicalOffice reMedicalOffice : handlingHospitalsList) {
            // ME0032: Use the nameAbbreviation instead of the officeName
            if (StringUtils.isNotEmpty(reMedicalOffice.getNameAbbreviation())) {
                REMedicalOffice reMedicalOfficeTemp = reMedicalOffice.toBuilder()
                    .setOfficeName(reMedicalOffice.getNameAbbreviation())
                    .build();

                reMedicalOffice = reMedicalOfficeTemp;
            }

            responseList.add(new GetHandlingHospitalResponse((reMedicalOffice)));
        }

        // ME0032: Sorting by the nameKana
        responseList.sort(Comparator.comparing(GetHandlingHospitalResponse::getOfficeNameKana));

        return responseList;
    }

    /**
     * 事業所Idで担当病院取得
     * @return {@code REListHandlingHospitalsResponse}
     */
    public List<jp.drjoy.service.web.model.Office> listHandlingHospitals() {
        List<jp.drjoy.service.web.model.Office> offices = new ArrayList<>();
        List<GetHandlingHospitalResponse> listHandlingHospitals = new ArrayList<>();

        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);

        SecurityContext context = SecurityContextHolder.getContext();
        LoginInfo loginInfo = (LoginInfo)context.getAuthentication().getPrincipal();
        REListHandlingHospitalsByOfficeIdRequest request = REListHandlingHospitalsByOfficeIdRequest.newBuilder()
            .setOfficeId(loginInfo.getOfficeId()).build();
        REListHandlingHospitalsResponse response = stub.listHandlingHospitalsByOfficeId(request);

        if (response.getHandlingHostpitalsList().isEmpty()){
            return offices;
        }
        List<REMedicalOffice> handlingHospitalsList = response.getHandlingHostpitalsList();
        for (REMedicalOffice reMedicalOffice: handlingHospitalsList) {
            listHandlingHospitals.add(new GetHandlingHospitalResponse((reMedicalOffice)));
        }
        //取引先会社のオフィスId習得
        List<OfficeUser> officeUsers = new ArrayList<>();
        for (GetHandlingHospitalResponse item : listHandlingHospitals){
            OfficeUser officeUser =  getOfficeUser(item.getOfficeUserId(),item.isOtherHandling());
            officeUsers.add(officeUser);
        }

        if (officeUsers.isEmpty()){
            return offices;
        }
        //ユニーク オフィスId一覧
        officeUsers = officeUsers.stream().distinct().collect(Collectors.toList());

        // オフィスの必要な情報取得
        List<String> listId = new ArrayList<>();
        for (OfficeUser item : officeUsers){
            listId.add(item.getOfficeId());
        }

        REGetOfficesResponse reGetOfficesResponse = getOfficesResponse(
            REGetOfficesRequest.newBuilder().addAllId(listId).build());
        for (REGetOfficesResponse.Item item : reGetOfficesResponse.getItemsList()){
            offices.add( new jp.drjoy.service.web.model.Office(item));
        }
        return offices;
    }

    /**
     * Get HandingHospital which User logging handle in Drjoy System
     *
     * 担当病院取得
     * @return {@code REListHandlingHospitalsResponse}
     */
    public List<GetHandlingHospitalResponse> getHandlingHospitalsRegistered(boolean personalFlag) {
        List<GetHandlingHospitalResponse> responseList = new ArrayList<>();
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        SecurityContext context = SecurityContextHolder.getContext();
        LoginInfo loginInfo = (LoginInfo)context.getAuthentication().getPrincipal();
        REListHandlingHospitalsRequest request = REListHandlingHospitalsRequest.newBuilder()
            .setUserId(loginInfo.getUserId()).setOfficeId(loginInfo.getOfficeId())
            .setPersonalFlag(personalFlag).build();
        REListHandlingHospitalsResponse response = stub.listHandlingHospitals(request);

        List<REMedicalOffice> handlingHospitalsList = response.getHandlingHostpitalsList();
        for (REMedicalOffice reMedicalOffice : handlingHospitalsList) {
            if (reMedicalOffice.getOtherHandling() == false
                && reMedicalOffice.getRestriction().getNumber() == 0 ) {
                responseList.add(new GetHandlingHospitalResponse((reMedicalOffice)));
            }
        }

        return responseList;
    }

    /**
     * 担当病院登録
     * @param request {@code RECreateHandlingHospitalsRequest}
     */
    public void createHandlingHospitals(
        RECreateHandlingHospitalsRequest request) {

        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.createHandlingHospitals(request);

        try {
            reactionReportingService.updateUnseenSideMenuForPr(
                Collections.singletonList(Authoritys.getLoginInfo().getOfficeUserId()));
        } catch (Exception ex) {
            logger.error("Update side menu RR fail with message: ", ex);
        }
    }

    /**
     * 担当病院登録
     * @param request {@code RECreateHandlingHospitalsRequest}
     */
    public void createHandlingOtherHospitals(
        RECreateHandlingHospitalsRequest request) {

        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.createHandlingHospitals(request);
    }

    /**
     * 担当病院削除
     * @param request {@code REDeleteHandlingHospitalsRequest}
     */
    public void deleteHandlingHospitals(
        REDeleteHandlingHospitalsRequest request) {

        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.deleteHandlingHospitals(request);

        try {
            reactionReportingService.updateUnseenSideMenuForPr(
                Collections.singletonList(Authoritys.getLoginInfo().getOfficeUserId()));
        } catch (Exception ex) {
            logger.error("Update side menu RR fail with message: ", ex);
        }
    }

    /**
     * 社員招待
     * @param request {@code InviteUsersRequest}
     * @return {@code InviteUsersResponse}
     */
    public REInvitePrUsersResponse invitePrUsers(REInvitePrUsersRequest request) {

        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.invitePrUsers(request);
    }

    /**
     * Dr一括招待
     * @param request {@code REInviteUsersRequest}
     * @return {@code REInviteUsersResponse}
     */
    public REInviteUsersResponse inviteUsers(REInviteUsersListRequest request) {

        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.inviteUsers(request);
    }

    /**
     * メール送信処理
     * @param request {@code RESendMailRequest}
     * @return {@code RESendMailResponse}
     */
    public RESendMailResponse sendMail(RESendMailRequest request) {

        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.sendMail(request);
    }

    /**
     * 仮登録ユーザ
     * @param request {@code REListProvisionalUsersRequest}
     * @return {@code REListProvisionalUsersResponse}
     */
    public REListProvisionalUsersResponse getProvisionalUsers(REListProvisionalUsersRequest request) {

        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.listProvisionalUsers(request);
    }

    /**
     * Download list provisional users
     * @param request
     * @return
     */
    public REDownloadProvisionalUsersResponse downloadProvisionalUsers(REListProvisionalUsersRequest request) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.downloadProvisionalUsers(request);
    }

    /**
     * ユーザ情報を取得する。
     *
     * @param userId   ユーザID
     * @param officeId 事業所ID
     */
    public REPrUser getPrUser(String userId, String officeId) {

        REGetPrUserRequest request = REGetPrUserRequest.newBuilder()
            .setUserId(userId).setOfficeId(officeId).build();
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getPrUser(request);
    }

    /**
     *  Update information PrUser
     *
     * @param rePrUser rePrUser request
     */
    public void putPrUser(REPrUser rePrUser){
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.putPrUser(rePrUser);
    }

    /**
     *  get list meeting configure
     *
     * @param depment String
     * @param userName String
     */
    public GetStaffListResponse getListMeetingConfigure(String depment, String userName, Integer page, Integer pageSize) {

        RegistrationGrpc.RegistrationBlockingStub stubegister = RegistrationGrpc.newBlockingStub(registrationChannel);
        REListMeetingConfigureRequest configureRequest = REListMeetingConfigureRequest.newBuilder()
            .setDepartmentId(depment)
            .setUserName(userName)
            .setPageable(CMNPage.newBuilder().setPage(page).setSize(pageSize).build())
            .build();
        REListMeetingConfigureResponse configureResponse = stubegister.listMeetingConfigure(configureRequest);
        return new GetStaffListResponse(configureResponse);
    }

    public GetPrepareCreateInsideGroupResponse prepareCreateInsideGroup(){
        return  new GetPrepareCreateInsideGroupResponse(getStaffInside());
    }

    public REGetStaffInsideResponse getStaffInside(){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        return  stub.getStaffInside(Empty.getDefaultInstance());
    }

    /**
     * prepareCreateInsideRoom
     * @return GetPrepareCreateInsideRoomResponse
     */
    public GetPrepareCreateInsideRoomResponse prepareCreateInsideRoom(){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub
            stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetStaffInsideResponse response = stub.getStaffInside(Empty.getDefaultInstance());
        return  new GetPrepareCreateInsideRoomResponse(response);
    }

    /**
     *  Get list office's Departments
     * @return REGetDepartmentResponse
     */
    public REGetDepartmentResponse getDepartments() {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getDepartments(Empty.getDefaultInstance());
    }

    /**
     * List all user of office
     * @param input email/userId input
     * @return REGetUserInfoByEmailResponse
     */
    public REGetUserInfoByEmailResponse getOfficeUsers(String input){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetUserInfoByEmailRequest request = REGetUserInfoByEmailRequest.newBuilder()
            .setEmail(input)
            .setOfficeId(Authoritys.getLoginInfo().getOfficeId())
            .build();
        return stub.getUserInfoByEmail(request);
    }

    public PrepareCreateOutsideGroupResponse prepareCreateOutsideGroup() {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetStaffOutsideResponse response = stub.getStaffOutside(Empty.getDefaultInstance());

        return new PrepareCreateOutsideGroupResponse(response);
    }

    /**
     * prepareCreateOutsideRoom
     * @return GetPrepareCreateOutsideRoomResponse
     */
    public GetPrepareCreateOutsideRoomResponse prepareCreateOutsideRoom() {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetStaffOutsideResponse response = stub.getStaffOutside(Empty.getDefaultInstance());

        return new GetPrepareCreateOutsideRoomResponse(response);
    }

    public OutsideOfficeUserRespone getOutsideOfficeUserFromEmail(String input) {
        if (Strings.isEmpty(input)){
            return new OutsideOfficeUserRespone(Collections.emptyList());
        }
        REGetUserInfoByEmailResponse reGetUserInfoByEmailResponse = getOfficeUsers(input);

        List<REUserInfoFromEmail> reUserInfoFromEmails = reGetUserInfoByEmailResponse.getUserList();

        if (reUserInfoFromEmails != null && !reUserInfoFromEmails.isEmpty()){
            return new OutsideOfficeUserRespone(reUserInfoFromEmails);
        } else{
            return new OutsideOfficeUserRespone(Collections.emptyList());
        }
    }

    /**
     *  get prepare edit inside group from response
     * @return REGetPrepareEditInsideGroupResponse
     */
    public REGetPrepareEditInsideGroupResponse getPrepareEditInsideGroupResponse(){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub
            = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        return  stub.prepareEditInsideGroup(Empty.getDefaultInstance());
    }

    /**
     * GR0003 Get list user info by department
     *
     * @param departmentId String
     * @return List User
     */
    public REGetDepartmentUserResponse getDepartmentUsers(String departmentId) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetDepartmentUserRequest.Builder requestBuilder = REGetDepartmentUserRequest.newBuilder();
        if (!Strings.isEmpty(departmentId))
            requestBuilder.setDeptId(departmentId);
        return stub.getDepartmentUser(requestBuilder.build());
    }

    /**
     * GR0005 Get department by id
     *
     * @param departmentId String
     * @return List User
     */
    public REDepartment getDepartment(String departmentId) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetDepartmentRequest.Builder deptBuilder = REGetDepartmentRequest.newBuilder();
            deptBuilder.setDeptId(departmentId);
        return stub.getDepartment(deptBuilder.build());
    }

    public GetPrepareCreateInsideDepartmentGroupResponse getPrepareCreateInsideDepartmentGroup() {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetPrepareCreateInsideDepartmentGroupResponse response = stub.prepareCreateInsideDepartmentGroup(Empty.getDefaultInstance());
        return new GetPrepareCreateInsideDepartmentGroupResponse(response.getDepartmentsList());
    }

    public REGetUserByListResponse getUserBasicInfoByList(List<String> officeUserIds){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub
            stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        REGetUserByListRequest request = REGetUserByListRequest.newBuilder()
            .addAllOfficeUserId(officeUserIds)
            .build();
        return stub.getListMemberInfo(request);
    }

    public List<PrepareOutsideOfficeUser> getPrepareOutsideOfficeUser(List<String> officeUserIds){
        List<PrepareOutsideOfficeUser> result = new ArrayList<>();
        if (!result.equals(REGetUserByListResponse.getDefaultInstance())) {
            REGetUserByListResponse userByList = getUserBasicInfoByList(officeUserIds);
            List<REUserByListInfo> userByListInfos = userByList.getUserByListInfoList();

            if (!userByListInfos.isEmpty()) {
                userByListInfos.forEach(user -> result.add(new PrepareOutsideOfficeUser(user)));
            } else {
                return Collections.emptyList();
            }
        } else{
            return Collections.emptyList();
        }

        return result;
    }

    public List<PrepareDepartmentUser> getDepartmentUserByList(List<String> officeUserIds){
        List<PrepareDepartmentUser> result = new ArrayList<>();

        List<String> reGetUserByList = new ArrayList<>();
        officeUserIds.forEach(officeUserId ->
            reGetUserByList.add(officeUserId));

        REGetUserByListRequest request = REGetUserByListRequest.newBuilder()
            .addAllOfficeUserId(reGetUserByList)
            .build();

        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub
            stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetUserByListResponse userByList = stub.getUserByList(request);
        List<REUserByListInfo> userByListInfos = userByList.getUserByListInfoList();

        if (!userByListInfos.isEmpty()){
            userByListInfos.forEach(user -> result.add(new PrepareDepartmentUser(user,"")));
        } else {
            return Collections.emptyList();
        }

        return result;
    }
    /**
     * Change status of MR
     *
     * @param request info identifyStatus which will be updated
     */
    public void putIdentifyStatus(REPutIdentifyStatusRequest request){
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.putIdentifyStatus(request);
    }

    /**
     * Change status MeetingRestriction of MR
     *
     * @param request meetingRestriction status will be updated
     */
    public void putMeetingRestriction(REPutMeetingRestrictionRequest request){
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.putMeetingRestriction(request);
    }

    /**
     * Get list asignee of office logging
     *
     * @param keyWord
     * @param next
     * @param  prev
     * @param pageSize
     * @return GetPICResponse
     */
    public GetPICResponse listAsignees(String keyWord, String next, String prev, int pageSize, boolean onlyUnconfirm, String sortBy){
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        REListAsigneesRequest request = REListAsigneesRequest.newBuilder()
            .setKeyword(Strings.nvl(keyWord))
            .setNext(Strings.nvl(next))
            .setLimit(pageSize)
            .setPrev(Strings.nvl(prev))
            .setOnlyUnconfirmed(onlyUnconfirm)
            .setSortBy(REListAsigneesRequest.SortBy.valueOf(sortBy))
            .build();
        return new GetPICResponse(stub.listAsignees(request));
    }

    /**
     * ME0003 get listVisitableUsers by officeId
     *
     * @param officeId String
     * @return {@code GetVisitableUsersResponse}
     */
    public GetVisitableUsersResponse listVisitableUsers(String userId,String officeId, int flag) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REListVisitableUsersRequest request = REListVisitableUsersRequest.newBuilder()
            .setUserId(userId)
            .setOfficeId(officeId)
            .setFlag(flag)
            .build();
        REListVisitableUsersResponse response = stub.listVisitableUsers(request);
        return new GetVisitableUsersResponse(response);
    }
    /**
     * Get list asignee history of office logging
     *
     * @param keyWord
     * @return GetPICResponse
     */
    public GetPICResponse listAsigneeHistory(String keyWord){
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        REListAsigneesHistoryRequest request = REListAsigneesHistoryRequest.newBuilder()
            .setOfficeName(keyWord)
            .build();
        return new GetPICResponse(stub.listAsigneesHistory(request));
    }

    /**
     *
     * @param keyWord
     * @return
     */
    public GetAllPICResponse getAllPic(String keyWord){
        GetAllPICResponse response = new GetAllPICResponse();
        response.setCurrentPic(listAsignees(keyWord, null, null, Integer.MAX_VALUE, false, SORT_BY_DEFAULT_LIST_MR).getPics());
        response.setHistoryPic(listAsigneeHistory(keyWord).getPics());
        response.getHistoryPic().forEach(pic -> {
            logger.debug("getAllPic response " + pic.getLastName()+""+ pic.getFirstName());
        });
        return response;
    }

    public REGetOfficesResponse getOfficesResponse(REGetOfficesRequest request) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getOffices(request);
    }

    public REListOfficeByIndustriesResponse listOfficeByIndustries(List<String> industryIds){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        REListOfficeByIndustriesRequest request = REListOfficeByIndustriesRequest.newBuilder()
            .addAllIndustryIds(industryIds)
            .build();
        return stub.listOfficeByIndustries(request);
    }

    public REGetOfficesResponse getOffice(String officeId){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub
            stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetOfficesRequest request = REGetOfficesRequest.newBuilder().addId(officeId).build();
        return stub.getOffices(request);
    }

    //TODO : confirm customer flow check password of user
    public Boolean isCheckPassword (String userId , String password){
        RegistrationGrpc.RegistrationBlockingStub stub =
           RegistrationGrpc.newBlockingStub(registrationChannel);

        RECheckPasswordRequest request = RECheckPasswordRequest.newBuilder()
            .setUserId(userId)
            .setPassword(password)
            .build();
        RECheckPasswordResponse response = stub.checkPassword(request);
        return response.getIsPassword();
    }

    /**
     *
     */
    public jp.drjoy.core.autogen.grpc.registration.GetHandlingHospitalResponse getStatusMeetingRestricted(String userId, String officeId, String handleOfficeId){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        GetHandlingHospitalRequest request = GetHandlingHospitalRequest.newBuilder()
            .setUserId(Strings.nvl(userId))
            .setOfficeId(Strings.nvl(officeId))
            .setHandleOfficeId(Strings.nvl(handleOfficeId))
            .build();
        return stub.getHandlingHospital(request);
    }


    public REListBlockUsersResponse getListBlockedUser(String userId, String officeId) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        REListBlockUsersRequest request = REListBlockUsersRequest.newBuilder()
            .setUserId(userId)
            .setOfficeId(officeId)
            .build();
        return   stub.listBlockUsers(request);
    }

    public GetListStaffResponse getStaffs(String keyword, boolean flagDoctor,
                                          boolean flagPharmacy, boolean flagOther, String officeIds,
                                          String fieldIds, int page, int pageSize) {
        if (!flagDoctor && !flagPharmacy && !flagOther) {
            return null;
        }

        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        LoginInfo loginInfo = Authoritys.getLoginInfo();
        REListStaffRequest request = REListStaffRequest.newBuilder()
            .setFlagDoctor(flagDoctor)
            .setFlagPharmacist(flagPharmacy)
            .setFlagOther(flagOther)
            .setKeyWord(Strings.nvl(keyword))
            .setOfficeUserId(loginInfo.getOfficeUserId())
            .setUserId(loginInfo.getUserId())
            .setOfficeId(loginInfo.getOfficeId())
            .addAllOfficeIds(getStringList(officeIds))
            .addAllFieldIds(getStringList(fieldIds))
            .setPage(page)
            .setPageSize( pageSize)
            .build();

        REListStaffResponse reResponse = stub.getStaffs(request);

        GetListStaffResponse response = getListStaffResponse(reResponse);

        Set<String> doctorOfficeUserIds = response.getDoctors().stream()
            .map(GetListStaffResponse.Staff::getOfficeUserId).collect(Collectors.toSet());;

        // check existed room between staffs vs mr
        RTCheckMultiExistedRoomResponse res = rtmService.checkMultiExistoomResponse(loginInfo.getOfficeUserId(),
            doctorOfficeUserIds);

        response.getDoctors().forEach(doctor -> {
            res.getResponseList().stream()
                .filter(r -> r.getDr().equals(doctor.getOfficeUserId()))
                .findFirst()
                .ifPresent(r -> doctor.setExisted_room(r.getIsExisted()));
        });

        return response;
    }

    private GetListStaffResponse getListStaffResponse(REListStaffResponse response) {
        GetListStaffResponse staffResponse = new GetListStaffResponse();

        List<GetListStaffResponse.Staff> list = new ArrayList<>();
        staffResponse.setTotalNumber(response.getTotalNumber());

        response.getStaffsList().forEach(meStaff -> {
            GetListStaffResponse.Staff staff = new GetListStaffResponse.Staff();

            staff.setOfficeUserId(meStaff.getOfficeUserId());
            staff.setUserId(meStaff.getUserId());
            staff.setOfficeId(meStaff.getOfficeId());
            staff.setOfficeName(meStaff.getOfficeName());
            staff.setOfficeNameAbbreviation(meStaff.getOfficeNameAbbreviation());

            staff.setOfficeUserId(meStaff.getOfficeUserId());
            staff.setFirstName(meStaff.getFirstName());
            staff.setLastName(meStaff.getLastName());
            staff.setFirstNameKana(meStaff.getFirstNameKana());
            staff.setLastNameKana(meStaff.getLastNameKana());
            staff.setImage(meStaff.getImage());

            if (StringUtils.isNotBlank(meStaff.getJobType())) {
                try {
                    JobType jobType = masterService.getJobType(meStaff.getJobType());
                    if (jobType != null) {
                        staff.setJobType(jobType.getJobName());
                    }
                } catch (StatusRuntimeException ex) {
                    logger.error("getListStaffResponse() -> getJobType() -> " + meStaff.getJobType() + ex.getMessage());
                }
            }

            staff.setPinned(meStaff.getPinned());
            staff.setMediator(meStaff.getMediator());
            staff.setTemporaryRegister(meStaff.getTemporaryRegister());

            RESpecializedDepartment reSpecializedDepartment = meStaff.getSpecializeDepartment();
            GetListStaffResponse.SpecializeDepartment specializeDepartment = new GetListStaffResponse.SpecializeDepartment();
            specializeDepartment.setTypeId(reSpecializedDepartment.getTypeId());

            String fieldId = reSpecializedDepartment.getFieldId();
            if (StringUtils.isNotBlank(fieldId)) {
                specializeDepartment.setFieldId(fieldId);
                try {
                    SpecialtyArea area = masterService.getSpecialtyArea(fieldId);
                    if (area != null) {
                        specializeDepartment.setNameField(area.getSpecialtyAreaName());
                    }
                } catch (StatusRuntimeException ex) {
                    logger.error("getListStaffResponse() -> getFieldName() -> " + reSpecializedDepartment.getFieldId() + ex.getMessage());
                }
            }

            staff.setMeetingRule(meStaff.getMeetingRule());
            staff.setSpecializeDepartment(specializeDepartment);

            GetListStaffResponse.MeetingDemand meetingDemand = new GetListStaffResponse.MeetingDemand();
            meetingDemand.setDetails(meStaff.getDetail());
            meStaff.getDemandsList().stream().forEach(demandMeeting -> {
                GetListStaffResponse.Demand demand = new GetListStaffResponse.Demand();
                demand.setDemandId(demandMeeting.getMeetingDemandId());
                demand.setLabel(demandMeeting.getLabel());
                meetingDemand.getDemandList().add(demand);
            });

            staff.setMeetingDemand(meetingDemand);

            if (StringUtils.isNotBlank(meStaff.getDetail()) || meStaff.getDemandsCount() > 0) {
                staff.setMeetingDemandStatus(true);
            }

            if (StringUtils.isNotBlank(meStaff.getMeetingRule())) {
                staff.setMeetingRuleStatus(true);
            }
            // flag accept meeting request
            staff.setAcceptMeetingRequest(meStaff.getAcceptMeetingRequest());

            list.add(staff);
        });

        staffResponse.setDoctors(list);

        return staffResponse;
    }

    private List<String> getStringList(String source) {
        if (StringUtils.isBlank(source)) {
            return Collections.EMPTY_LIST;
        }

        return Arrays.stream(source.split(TOKEN)).collect(Collectors.toList());
    }

    public void pinStaff(PinStaffRequest request) {
        REPinStaffRequest rePinStaffRequest = REPinStaffRequest.newBuilder()
            .setUserId(Authoritys.getLoginInfo().getUserId())
            .setOfficeId(Authoritys.getLoginInfo().getOfficeId())
            .setStaffUserId(Strings.nvl(request.getStaffUserId()))
            .setStaffOfficeId(Strings.nvl(request.getStaffOfficeId()))
            .setPinned(request.isPinned())
            .build();
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        stub.pinStaff(rePinStaffRequest);
    }

    public void updateImageProfile(REUpdateImageProfileRequest request) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.updateImageProfile(request);
    }

    public void updateImageProfileOnly(REUpdateImageProfileOnlyRequest request) {
        RegistrationGrpc.RegistrationBlockingStub stub =
            RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.updateImageProfileOnly(request);
    }

    public REUserListRespone getListUser(Map<String, Set<String>> map) {

        REUserListRequest.Builder request = REUserListRequest.newBuilder();
        map.keySet().forEach(officeId -> {
            Set<String> set = map.get(officeId);
            for (String userId : set) {
                REUserRequest.Builder userRequest = REUserRequest.newBuilder().setUserId(Strings.nvl(userId))
                    .setOfficeId(Strings.nvl(officeId));
                request.addUserRequest(userRequest);
            }
        });

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getListUser(request.build());
    }
    public Boolean checkOfficeLogin(String officeId){
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return  Office.isPharmacyOffice(stub.getOffice(
            GetOfficeRequest.newBuilder().setId(officeId).setDetail(false).build()));
    }

    /** 取引先事業所作成 */
    public String registerPharmacyOffice(jp.drjoy.service.web.model.Office maPharmacyOffice) {

        // リクエスト設定
        RERegisterPharmacyOfficeRequest.Builder builder =
            RERegisterPharmacyOfficeRequest.newBuilder();
        builder.setPharmacyOfficeId(maPharmacyOffice.getId());
        builder.setName(Strings.nvl(maPharmacyOffice.getName()));
        builder.setNameKana(Strings.nvl(maPharmacyOffice.getNameKana()));
        builder.setNameInitial(Strings.nvl(maPharmacyOffice.getNameInitial()));
        builder.setNameAbbreviation(Strings.nvl(maPharmacyOffice.getShortName()));
        builder.setIsTestOffice(maPharmacyOffice.isTestOffice());

        // 取引先事業所登録
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        RERegisterPharmacyOfficeResponse reRegisterPharmacyOfficeResponse =
            stub.registerPharmacyOffice(builder.build());

        return reRegisterPharmacyOfficeResponse.getOfficeId();
    }

    // forget password
    public void forgetPassword(String email, Product product) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REForgetPasswordRequest request = REForgetPasswordRequest.newBuilder()
            .setEmail(email)
            .setProduct(product.name())
            .build();
        stub.forgetPassword(request);
    }

    //  resetPassword
    public void resetPassword(ResetPasswordRequest request, Product product) {

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REResetPasswordRequest resetRequest = REResetPasswordRequest.newBuilder()
            .setTocken(request.getToken())
            .setPassword(request.getPassword())
            .setBirthDate(request.getBirthDate())
            .setProduct(product.name())
            .build();
        stub.resetPassword(resetRequest);

    }

    // update identify status of MR
    public void  updateIdentifyStatus(){

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        stub.updateIdentify(Empty.getDefaultInstance());
    }


    /**
     * Get list officeUserId of user by officeId
     *TODO: Remove later
     *
     * @param officeId officeId
     * @return List OfficeUserId
     */
    public List<String> getListUser(String officeId) {
        List<String> officeUserIds = new ArrayList<>();
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        UTLGetListUserResponse response = stub.getListUser(UTLGetListUserRequest.newBuilder()
            .setOfficeId(officeId != null ? officeId : Strings.EMPTY).build());
        if (!response.getOfficeUserIdsList().isEmpty()) {
            officeUserIds = response.getOfficeUserIdsList();
        } else {
            officeUserIds = Collections.EMPTY_LIST;
        }

        return officeUserIds;
    }

    public REListUser getHandleUsers(List<MEListUserIdAndOfficeId> userList){
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);

        REGetUserListRequest request = REGetUserListRequest.newBuilder().addAllListUserIdAndOfficeId(userList).build();

        return stub.getUserList(request);
    }

    public List<GetPharmacyOfficeResponse> getListAllHandingHospital(String officeId, String officeUserId) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        MEGetListAllHandlingRequest request = MEGetListAllHandlingRequest.newBuilder()
            .setOfficeId(officeId)
            .setOfficeUserId(officeUserId)
            .build();
        List<REPharmacyOffice> rePharmacyOfficeList = stub.getListAllHandingHospital(request).getPharmacyOfficesList();
        List<GetPharmacyOfficeResponse> pharmacyOfficeList = new ArrayList<>();

        for (REPharmacyOffice rePharmacyOffice : rePharmacyOfficeList){
            pharmacyOfficeList.add(new GetPharmacyOfficeResponse(rePharmacyOffice));
        }
        return pharmacyOfficeList ;
    }

    public REGetUserByListResponse getUserInfoList(Set<String> officeUserIds) {

        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetUserByListRequest request = REGetUserByListRequest.newBuilder().addAllOfficeUserId(officeUserIds).build();

        return stub.getUserByList(request);
    }

    /**
     * Get user by OfficeUserIds or UserIds
     * @param officeUserIds
     * @param userIds
     * @return
     */
    public REGetUserByListResponse getUserInfoListByOfficeUserIdsOrUserIds(Set<String> officeUserIds, Set<String> userIds) {

        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetUserByOfficeUserIdsOrUserIdsRequest.Builder request = REGetUserByOfficeUserIdsOrUserIdsRequest.newBuilder();
        request.addAllOfficeUserId(officeUserIds);
        request.addAllOfficeUserId(userIds);

        return stub.getUserInfoListByOfficeUserIdsOrUserIds(request.build());
    }

    public List<GetPharmacyOfficeResponse> convertToListGetPharmacyOfficeResponse(REListCustomerOfficesResponse response) {
        List<GetPharmacyOfficeResponse> list = new ArrayList<>();

        response.getOfficesList().forEach(reCustomerOffice -> {
            GetPharmacyOfficeResponse office = new GetPharmacyOfficeResponse(reCustomerOffice);
            list.add(office);
        });

        return list;
    }

    public REGetPrUserProfileResponse getPrUserProfile(String officeUserId) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetPrUserProfileRequest request = REGetPrUserProfileRequest.newBuilder()
            .setOfficeUserId(Strings.nvl(officeUserId)).build();
        return stub.getPrUserProfile(request);
    }

    public List<User> getListDrWithCondition(String officeId, String typeId, String keyword) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REGetListDrWithConditionRequest request = REGetListDrWithConditionRequest.newBuilder()
            .setOfficeId(Strings.nvl(officeId))
            .setTypeId(Strings.nvl(typeId))
            .setKeyword(Strings.nvl(keyword)).build();
        return convertToListUser(stub.getListDrWithCondition(request));
    }

    private List<User> convertToListUser(REGetListDrWithConditionResponse response) {
        List<User> users = new ArrayList<>();
        if (!CollectionUtils.isEmpty(response.getDrItemList())) {
            response.getDrItemList().forEach(reDrItem -> {
                User user = new User();
                user.setOfficeUserId(reDrItem.getOfficeUserId());
                user.setUserId(reDrItem.getUserId());
                user.setOfficeId(reDrItem.getOfficeId());
                user.setDepartmentId(reDrItem.getDepartmentId());
                user.setFirstName(reDrItem.getFirstName());
                user.setLastName(reDrItem.getLastName());
                user.setAvatar(reDrItem.getAvatar());
                user.setAccountStatus(reDrItem.getAccountStatus());

                users.add(user);
            });
        }
        return users;
    }

    public OfficeSetting getOfficeSetting(){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub exterStub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetOfficeForAMRequest request = REGetOfficeForAMRequest.newBuilder()
            .setOfficeId(Authoritys.getLoginInfo().getOfficeId())
            .build();
        REGetOfficeForAMResponse response = exterStub.getOfficeForAM(request);
        return new OfficeSetting(response);
    }

    public OfficeSetting getOfficeSetting(String officeId){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub exterStub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetOfficeForAMRequest request = REGetOfficeForAMRequest.newBuilder()
            .setOfficeId(Strings.nvl(officeId))
            .build();
        REGetOfficeForAMResponse response = exterStub.getOfficeForAM(request);
        return new OfficeSetting(response);
    }

    public PrUserItem getDetailMrUser(String officeUserId) {

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REGetMrUserRequest request = REGetMrUserRequest.newBuilder().setOfficeUserId(officeUserId).build();
        REGetMrUserResponse response = stub.getMRUser(request);

        return new PrUserItem(response.getUser());
    }

    public RestrictedOfUserResponse getRestrictedOfUser(String drOfficeUserId , String mrOfficeUserId){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub exterStub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        REGetRestrictedOfUserRequest request = REGetRestrictedOfUserRequest.newBuilder()
            .setDrOfficeUserId(drOfficeUserId)
            .setMrOfficeUserId(mrOfficeUserId)
            .build();

        REGetRestrictedOfUserResponse response = exterStub.getRestrictedOfUser(request);
        return new RestrictedOfUserResponse(response);
    }

    public REListMedicalOfficeUserResponse listMedicalOfficeUser(REListMedicalOfficeUserRequest request) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.listMedicalOfficeUser(request);
    }

    public List<REMrShareInfoStatus> listMrShareInfoStatus(boolean isDr,
                                                           List<String> drOfficeUserIds,
                                                           List<String> mrOfficeUserIds) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REListMrShareInfoStatusRequest request = REListMrShareInfoStatusRequest.newBuilder()
            .setIsDr(isDr)
            .addAllDrOfficeUserId(drOfficeUserIds)
            .addAllMrOfficeUserId(mrOfficeUserIds)
            .build();
        REListMrShareInfoStatusResponse response = stub.listMrShareInfoStatus(request);
        return response.getStatusList();
    }

    public boolean putMrShareInfoStatus(PutSettingDrRequest request) {
        LoginInfo loginInfo = Authoritys.getLoginInfo();
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REPutMrShareInfoStatusResponse response = stub.putMrShareInfoStatus(request.asREPutMrShareInfoStatusRequest(loginInfo));
        return response.getSelectedAll();
    }

    //  Update image profile and identification image
    public void updateImageProfileAndIdentificationImage(String officeUserId, String imageUrlFireBase, String identificationImageUrlFireBase) {

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        UpdateImageProfileAndIdentificationImageRequest resetRequest = UpdateImageProfileAndIdentificationImageRequest.newBuilder()
            .setOfficeUserId(Strings.nvl(officeUserId))
            .setImageUrlFireBase(Strings.nvl(imageUrlFireBase))
            .setIdentificationImageUrlFireBase(Strings.nvl(identificationImageUrlFireBase))
            .build();
        stub.updateImageProfileAndIdentificationImage(resetRequest);

    }

    public REGetUserByListResponse getListMemberInfo (List<String> officeUserIds){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub
            stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        REGetUserByListRequest request = REGetUserByListRequest.newBuilder()
            .addAllOfficeUserId(officeUserIds)
            .build();
        return stub.getListMemberInfo(request);
    }

    public GetListHandleUsersResponse getListHandleUsers(String userId, String officeId) {
        RegistrationGrpc.RegistrationBlockingStub
            stub = RegistrationGrpc.newBlockingStub(registrationChannel);

        REUserRequest request = REUserRequest.newBuilder()
            .setUserId(userId)
            .setOfficeId(officeId)
            .build();
        REUserListRespone listHandleUsers = stub.getListHandleUsers(request);
        return new GetListHandleUsersResponse(listHandleUsers);
    }

    public GetDrugStoreByEmailResponse getDrugStoreUserByEmail(String email){
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetDrugStoreByEmailRequest request = REGetDrugStoreByEmailRequest.newBuilder().setEmail(email).build();
        REGetDrugStoreByEmailResponse response = stub.getDrugStoreUserByEmail(request);
        REUserInfoFromEmail drugStoreUser = response.getDrugStoreUser();
        if (drugStoreUser == null || drugStoreUser.toString().equals(Strings.EMPTY)){
            return null;
        }
        return new GetDrugStoreByEmailResponse(drugStoreUser);
    }

    //Find list office user by office id
    public List<OfficeUser> getByOfficeIds(List<String> officeIds) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REListUserByOfficesRequest.Builder requestBuilder = REListUserByOfficesRequest.newBuilder();
        requestBuilder.addAllOfficeIds(officeIds);
        REListUserByOfficesResponse response = stub.listUserByOffices(requestBuilder.build());
        List<REOfficeUser> officeUsers = response.getUserList();
        return officeUsers.stream().map(reOfficeUser -> {
            OfficeUser officeUser = new OfficeUser();
            officeUser.setId(reOfficeUser.getOfficeUserId());
            officeUser.setFirstName(reOfficeUser.getFirstName());
            officeUser.setLastName(reOfficeUser.getLastName());
            officeUser.setFirstNameKana(reOfficeUser.getFirstNameKana());
            officeUser.setLastNameKana(reOfficeUser.getLastNameKana());
            Profile profile = new Profile();
            profile.setImage(reOfficeUser.getImageFileId());
            officeUser.setProfile(profile);
            return officeUser;
        }).collect(Collectors.toList());
    }

    /**
     * A0006 SSOユーザー情報取得
     */
    public REGetSSOTokenUserInfoResponse getSSOTokenUserInfo() {
        RegistrationSSOGrpc.RegistrationSSOBlockingStub stub =
            RegistrationSSOGrpc.newBlockingStub(registrationChannel);

        return stub.getSSOTokenUserInfo(REGetSSOTokenUserInfoRequest.getDefaultInstance());
    }

    public REGetPharmacyOfficeResponse getPharmacyOffice(String officeId) {
        REGetPharmacyOfficeRequest request = REGetPharmacyOfficeRequest.newBuilder()
            .setOfficeId(officeId)
            .build();

        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        return stub.getPharmacyOffice(request);
    }

    public REGetListPharmacyOfficeResponse getListPharmacyOffice(List<String> officeIds) {
        REGetListPharmacyOfficeRequest request = REGetListPharmacyOfficeRequest.newBuilder()
            .addAllOfficeIds(officeIds)
            .build();

        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        return stub.getListPharmacyOffice(request);
    }

    public REGetDepartmentResponse getREGetDepartmentResponse() {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REGetDepartmentsByOfficeIdRequest request = REGetDepartmentsByOfficeIdRequest.newBuilder()
            .setOfficeId(getLoginInfo().getOfficeId())
            .build();
        return stub.getDepartmentsByOfficeId(request);
    }
    public REGetUserGrantServiceAttendanceResponse getUserGrantServiceWithCondition(String departmentId, String jobType, String userName,
                                                                                    String officeId)
    {
        REGetUserGrantServiceAttendanceRequest request = REGetUserGrantServiceAttendanceRequest.newBuilder()
            .setDepartmentId(departmentId.equals(DEFAULT_DEPARTMENT_FOR_ALL) ? StringUtils.EMPTY : departmentId)
            .setJobType(jobType.equals(DEFAULT_JOB_TYPE_FOR_ALL) ? StringUtils.EMPTY : jobType)
            .setKeyword(StringUtils.trimToEmpty(userName))
            .setOfficeId(officeId).build();
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.
            newBlockingStub(registrationChannel);
        return stub.getUserGrantServiceAttendance(request);
    }
    /**
     * get OfficeUser from officeUserId
     * @param officeUserId {@linkplain String}
     * @return {@link REOfficeUser}
     */
    public  REOfficeUser getOfficeUser(String officeUserId) {
        REGetOfficeUserRequest request =
            REGetOfficeUserRequest.newBuilder().setOfficeUserId(officeUserId).build();
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetOfficeUserResponse res = stub.getOfficeUser(request);
        return res.hasUser() ? res.getUser() : null;
    }

    /**
     * get List departmentId from parent Department
     *
     * @param departmentId {@linkplain String}
     * @return {@linkplain List of {@link String}}
     */

    public List<String> getListChildDepartmentIds(String departmentId) {
        List<String> departmentIds = new ArrayList<>();
        REDepartment currentDepartment = getDepartment(departmentId);
        if (currentDepartment != null) {
            departmentIds.add(currentDepartment.getId());
            // List child department
            List<REDepartment> departmentChildren = currentDepartment.getChildrenList();
            if (departmentChildren != null && !(org.springframework.util.CollectionUtils.isEmpty(
                departmentChildren))) {
                List<REDepartment> departmentAndChild =
                    new ArrayList<>(findAllChild(currentDepartment));
                for (REDepartment department : departmentAndChild) {
                    departmentIds.add(department.getId());
                }
            }
        }
        return departmentIds;
    }
    public List<String> getListDepartmentId(REDepartment department) {
        List<String> idList = new ArrayList<>();
        if (!StringUtils.isEmpty(department.getId())) {
            idList.add(department.getId());
        }
        if (department.getChildrenList().size() != 0) {
            for (REDepartment d : department.getChildrenList()) {
                idList.addAll(getListDepartmentId(d));
            }
        }
        return idList;
    }
    /**
     * get All Childrent Department
     *
     * @param department {@link REDepartment}
     * @return {@linkplain List of {@link REDepartment}}
     */
    public List<REDepartment> findAllChild(REDepartment department) {
        List<REDepartment> listResult = new ArrayList<>();
        List<REDepartment> childList = department.getChildrenList();
        if (childList != null && !childList.isEmpty()) {
            // Get all children
            listResult.addAll(childList);
            for (REDepartment child : childList) {
                listResult.addAll(findAllChild(child));
            }
        }
        return listResult;
    }
    /**
     //     * get Office by Id
     //     * @param officeId {@linkplain String}
     //     * @return {@link OfficeMessage}
     //     */
        public OfficeMessage getOfficeMessage(String officeId) {
            GetOfficeRequest getOfficeRequest = GetOfficeRequest.newBuilder()
                .setId(Strings.nvl(officeId))
                .setDetail(true)
                .build();
            RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
            return stub.getOffice(getOfficeRequest);
        }

    public REGetUserByListResponse getUserByList(REGetUserByListRequest reGetUserByListRequest) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getUserByList(reGetUserByListRequest);
    }

    public REGetListUserByConditionResponse getListUserByCondition(String departmentId,
                                                                   String keyword, String officeId) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getListUserByCondition(REGetListUserByConditionRequest.newBuilder()
            .setOfficeId(Strings.nvl(officeId))
            .setDepartmentId(Strings.nvl(departmentId))
            .setKeyword(Strings.nvl(keyword))
            .build());
    }

    public boolean getOfficeVpnConnectionSetting() {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub exterStub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetOfficeForAMRequest request = REGetOfficeForAMRequest.newBuilder()
            .setOfficeId(Authoritys.getLoginInfo().getOfficeId())
            .build();
        REGetOfficeForAMResponse response = exterStub.getOfficeForAM(request);

        logger.info("vpnConnectionSetting: {}", response.getVpnConnection());
        return response.getVpnConnection();
    }


    public REGetListPrUserForRRResponse findPrUserForRR(String medicalOfficeId, List<String> phOfficeIds) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        return stub.getListPrUserForRR(
            REGetListPrUserForRRRequest.newBuilder()
                .setMedicalOfficeId(Strings.nvl(medicalOfficeId))
                .addAllPharmacyOfficeId(phOfficeIds).build());
    }

    /**
     * getListHandlingHospitalsNowAndPast
     * @param officeUserIds
     * @return {@link ListOfficeResponse}
     */
    public ListOfficeResponse getListHandlingHospitalsNowAndPast(List<String> officeUserIds) {
        ListOfficeResponse responseList = new ListOfficeResponse();

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);

        REListHandlingHospitalsNowAndPastResponse response = stub.listHandlingHospitalsNowAndPast(REListHandlingHospitalsNowAndPastRequest.newBuilder()
            .addAllOfficeUserId(officeUserIds)
            .build());

        List<REMedicalOffice> handlingHospitalsList = response.getHandlingHostpitalsList();

        for (REMedicalOffice reMedicalOffice : handlingHospitalsList) {
            responseList.getOffices().add(new ListOfficeResponse.Office((reMedicalOffice)));
        }

        return responseList;
    }

    /**
     * get basic info of offices by list officeId
     *
     * @param officeIds {@link java.util.List<java.lang.String>}
     * @return {@link REGetListOfficeResponse}
     */
    public REGetListOfficeResponse getListOfficeById(List<String> officeIds) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);

        return stub.getListOffice(REGetListOfficeRequest.newBuilder()
            .addAllOfficeId(officeIds)
            .build());
    }

    /**
     * get list officeUserId of offices by list officeId
     *
     * @param officeIds {@link java.util.List<java.lang.String>}
     * @return {@link REGetListOfficeUserIdsByOfficeIdsResponse}
     */
    public REGetListOfficeUserIdsByOfficeIdsResponse getListOfficeUserIdsByOfficeIds(List<String> officeIds) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);

        return stub.getListOfficeUserIdsByOfficeIds(
            REGetListOfficeRequest.newBuilder()
                .addAllOfficeId(officeIds).build());
    }

    public List<GetHandlingHospitalResponse> getHandlingHospitalsWithKeyword(String keyword) {
        SecurityContext context = SecurityContextHolder.getContext();
        LoginInfo loginInfo = (LoginInfo) context.getAuthentication().getPrincipal();
        List<GetHandlingHospitalResponse> list = getHandlingHospitals(loginInfo.getUserId(), loginInfo.getOfficeId());
        return list.stream()
            .filter(item -> (CommonUtils.containIgnoreCase(keyword, item.getOfficeName())
                || CommonUtils.containIgnoreCase(keyword, item.getOfficeNameKana())))
            .sorted(Comparator.comparing(GetHandlingHospitalResponse::getOfficeNameKana)
                .thenComparing(GetHandlingHospitalResponse::getOfficeId))
            .collect(Collectors.toList());
    }

    public List<Office> getListOfficeActive(String keyword) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetListOfficeByConditionResponse response = stub.getListOfficeByCondition(REGetListOfficeByConditionRequest.newBuilder()
            .setKeyword(Strings.nvl(keyword))
            .addOfficeType(REOfficeType.MEDICAL)
            .addOfficeType(REOfficeType.DRUG_STORE)
            .setPage(CMNPage.newBuilder()
                .setSize(Integer.MAX_VALUE)
                .build())
            .build());

        List<Office> offices = new ArrayList<>();
        response.getOfficeList().forEach(item -> {
            Office office = new Office();
            office.setId(item.getId());
            office.setName(item.getName());
            office.setNameKana(item.getNameKana());
            offices.add(office);
        });

        return offices;
    }

    public REGetListMRByRoleResponse getListMRByRoleAndOfficeIds(String role, List<String> pharmacyOffices) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        return stub.getListMRByRoleAndOfficeIds(
            REGetListMRByRoleRequest.newBuilder()
                .setRole(Role.valueOf(role))
                .addAllPharmacyOffices(pharmacyOffices)
                .build());
    }

    public REGetListMRByRoleResponse getListMRByRoleAndOfficeUserIds(String role, List<String> officeUserIds) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        return stub.getListMRByRoleAndOfficeUserIds(
            REGetListMRByRoleRequest.newBuilder()
                .setRole(Role.valueOf(role))
                .addAllOfficeUserIds(officeUserIds)
                .build());
    }

    /** Prユーザ詳細取得 */
    public REGetPrUserDetailResponse getPrUserDetail(String officeUserId) {
        // リクエスト作成
        REGetPrUserDetailRequest reGetPrUserDetailRequest =
            REGetPrUserDetailRequest.newBuilder().setOfficeUserId(officeUserId).build();

        // Prユーザ詳細取得
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        return stub.getPrUserDetail(reGetPrUserDetailRequest);
    }

    REGetListHandleOfficeIdsByOfficeUserIdsResponse getListHandleOfficeIdsByOfficeUserIds(List<String> officeUserIds) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getListHandleOfficeIdsByOfficeUserIds(
            REGetListHandleOfficeIdsByOfficeUserIdsRequest.newBuilder().addAllOfficeUserId(officeUserIds).build());
    }
    /**
     * Get List Status of Dr
     *
     * @param drOfficeUserId
     * @param mrOfficeUserId
     * @param checkInvalid
     * @param checkLocked
     * @param checkLockMr
     * @param checkNoMeetingRole
     * @param checkRestrictMeeting
     * @param checkRestrictHospital
     * @param checkCancelHandlingHospital
     * @return
     */
    public REGetStatusOfDrResponse getStatusOfDr(String drOfficeUserId,
                                                 String mrOfficeUserId,
                                                 boolean checkInvalid,
                                                 boolean checkLocked,
                                                 boolean checkLockMr,
                                                 boolean checkNoMeetingRole,
                                                 boolean checkRestrictMeeting,
                                                 boolean checkRestrictHospital,
                                                 boolean checkCancelHandlingHospital) {
        REGetStatusOfDrRequest request = REGetStatusOfDrRequest.newBuilder()
            .setDrOfficeUserId(drOfficeUserId)
            .setMrOfficeUserId(mrOfficeUserId)
            .setCheckInvalid(checkInvalid)
            .setCheckLocked(checkLocked)
            .setCheckLockMr(checkLockMr)
            .setCheckNoMeetingRole(checkNoMeetingRole)
            .setCheckRestrictMeeting(checkRestrictMeeting)
            .setCheckRestrictHospital(checkRestrictHospital)
            .setCheckCancelHandlingHospital(checkCancelHandlingHospital)
            .build();
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        try {
            return stub.getStatusOfDr(request);
        } catch (Exception ex) {}

        return null;
    }

    /**
     * Get List Status of MR
     *
     * @param drOfficeUserId
     * @param mrOfficeUserId
     * @param checkInvalid
     * @param checkRestrict
     * @param checkCancelHandlingHospital
     * @param checkBlocked
     * @return
     */
    public REGetStatusOfMrResponse getStatusOfMr(String drOfficeUserId,
                                                 String mrOfficeUserId,
                                                 boolean checkInvalid,
                                                 boolean checkRestrict,
                                                 boolean checkCancelHandlingHospital,
                                                 boolean checkBlocked,
                                                 boolean checkSettingShareInfo) {
        REGetStatusOfMrRequest request = REGetStatusOfMrRequest.newBuilder()
            .setDrOfficeUserId(drOfficeUserId)
            .setMrOfficeUserId(mrOfficeUserId)
            .setCheckInvalid(checkInvalid)
            .setCheckRestrict(checkRestrict)
            .setCheckCancelHandlingHospital(checkCancelHandlingHospital)
            .setCheckBlocked(checkBlocked)
            .setCheckSettingShareInfo(checkSettingShareInfo)
            .build();
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        try {
            return stub.getStatusOfMr(request);
        } catch (Exception ex) {}

        return null;
    }

    public REGetStaffRequestByManagerIdResponse getStaffAuthorityAcceptedRequestOfManager(
        REGetStaffRequestByManagerIdRequest request) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getStaffRequestByManagerId(request);
    }

    public REGetUserGrantServiceAttendanceResponse getUserGrantServiceAttendanceFP15Department(String departmentId, String jobType, String userName, String officeId) {
        REGetUserGrantServiceAttendanceRequest request = REGetUserGrantServiceAttendanceRequest.newBuilder()
            .setDepartmentId(departmentId)
            .setJobType(jobType.equals(DEFAULT_JOB_TYPE_FOR_ALL) ? StringUtils.EMPTY : jobType)
            .setKeyword(StringUtils.trimToEmpty(userName))
            .setOfficeId(officeId).build();
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.
            newBlockingStub(registrationChannel);
        return stub.getUserGrantServiceAttendanceFP15Department(request);
    }

    public REGetListUserNameByListUserIdResponse getListUserNameByListOfficeUserId(List<String> officeUserIds, String officeId) {

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REGetListUserNameByListUserIdRequest.Builder builder = REGetListUserNameByListUserIdRequest.newBuilder();
        builder.setOfficeId(officeId).addAllOfficeUserId(officeUserIds);
        return stub.getListUserNameByListOfficeUserId(builder.build());
    }

    public REGetListUserByConditionResponse getListUserLevelLimitedByCondition(String departmentId,
                                                                               String keyword, String officeId) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getListUserLevelLimitedByCondition(REGetListUserByConditionRequest.newBuilder()
                                                           .setOfficeId(Strings.nvl(officeId))
                                                           .setDepartmentId(Strings.nvl(departmentId))
                                                           .setKeyword(Strings.nvl(keyword))
                                                           .build());
    }

    public REGetListUserByConditionResponse getListUserByRequestManagementAuthorityCondition(String departmentId,
                                                                                             String keyword,
                                                                                             String officeId,
                                                                                             List<String> officeUserIds) {

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getListUserByRequestManagementAuthorityCondition(
            REGetListUserByRequestManagementAuthorityConditionRequest.newBuilder()
                .setOfficeId(Strings.nvl(officeId))
                .setDepartmentId(Strings.nvl(departmentId))
                .setKeyword(Strings.nvl(keyword)).addAllOfficeUserId(officeUserIds)
                .build());
    }

    public REGetListDepartmentIdByOfficeIdResponse getListDepartmentIdByOfficeId(String officeId) {

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REGetListDepartmentIdByOfficeIdRequest.Builder builder = REGetListDepartmentIdByOfficeIdRequest.newBuilder();
        builder.setOfficeId(officeId);
        return stub.getListDepartmentIdByOfficeId(builder.build());
    }

    public REGetDepartmentResponse getListDepartmentByDepartmentId(List<String> departmentIds, List<String> officeUserIds) {

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REGetListDepartmentByOfficeIdRequest.Builder builder = REGetListDepartmentByOfficeIdRequest.newBuilder();
        builder.addAllDepartmentId(departmentIds).addAllOfficeUserId(officeUserIds);
        return stub.getListDepartmentByDepartmentId(builder.build());

    }

    /**
     * get list PruUsers by officeUserIds
     * @param officeUserIds list officeUserIds to get pr info
     * @return {@link REListPrUsersByOfficeUserIdsResponse}
     */
    REListPrUsersByOfficeUserIdsResponse getListPrUsersByOfficeUserIds(List<String> officeUserIds) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.listPrUsersByOfficeUserIds(
            REListPrUsersByOfficeUserIdsRequest.newBuilder().addAllOfficeUserIds(officeUserIds).build());
    }

    /**
     * Get DR info by officeUserId
     *
     * @param officeUserId
     * @return
     */
    public REUser getUserByOfficeUserId(String officeUserId) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);

        REGetUserByOfficeUserIdRequest request = REGetUserByOfficeUserIdRequest.newBuilder()
            .setOfficeUserId(Strings.nvl(officeUserId))
            .build();

        return stub.getUserByOfficeUserId(request);
    }

    /**
     * Get MR info by officerUserId
     *
     * @param officeUserId
     * @return
     */
    public REGetMrUserResponse getMRUserInfo(String officeUserId) {
        REGetMrUserRequest request = REGetMrUserRequest.newBuilder()
            .setOfficeUserId(Strings.nvl(officeUserId))
            .build();
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getMRUser(request);
    }
    public REGetStaffRequestItemResponse getStaffRequestItem(String officeId, List<String> officeUserIds, CMNPage cmnPage) {

        REGetStaffRequestItemRequest request = REGetStaffRequestItemRequest.newBuilder()
            .setOfficeId(officeId)
            .setPage(cmnPage)
            .addAllOfficeUserId(officeUserIds)
            .build();

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.
            newBlockingStub(registrationChannel);
        return stub.getStaffRequestItem(request);
    }

    public REGetListUserByOfficeIdResponse getListUserByOfficeId(String officeId, String managerRequestId, String userName,
                                                                 String userNameBtn, CMNPage cmnPage) {

        REGetListUserByOfficeIdRequest request = REGetListUserByOfficeIdRequest.newBuilder()
            .setOfficeId(officeId)
            .setKeyword(StringUtils.trimToEmpty(userName))
            .setUserNameBtn(userNameBtn)
            .setPage(cmnPage)
            .setManagerRequestId(managerRequestId)
            .build();

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.
            newBlockingStub(registrationChannel);
        return stub.getListUserByOfficeId(request);
    }

    public REListOfficeByListOfficeIdResponse listOfficeByListOfficeId(Set<String> officeIds) {
        REListOfficeByListOfficeIdRequest request = REListOfficeByListOfficeIdRequest.newBuilder()
            .addAllOfficeId(officeIds)
            .build();
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.listOfficeByListOfficeId(request);
    }

    public List<REUser> getAllOfficeUserByOfficeUserIds(Set<String> officeUserIds) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REListUserByOfficeUserIdResponse response = stub.listUserByOfficeUserId(REListUserByOfficeUserIdRequest
            .newBuilder().addAllOfficeUserId(officeUserIds).build());
        return response.getReUserList();
    }

    /**
     * Get list officeId MR handling hospital
     * @param officeUserId
     * @return
     */
    public List<String> getListOfficeIdHandlingHospital(String officeUserId) {
        REGetListOfficeIdHandlingHospitalRequest request = REGetListOfficeIdHandlingHospitalRequest.newBuilder()
            .setOfficeUserId(Strings.nvl(officeUserId))
            .build();

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REGetListOfficeIdHandlingHospitalResponse reGetListOfficeIdHandlingHospitalResponse = stub.getListOfficeIdHandlingHospital(request);
        return reGetListOfficeIdHandlingHospitalResponse.getOfficeIdList();
    }

    /**
     * List Office by keyword
     *
     * @param keyword String, value input is officeName and officeNameKana
     * @return list Office
     */
    public REListOfficeByListOfficeIdResponse listOfficeByKeyword(List<String> listOfficeId, String keyword) {
        REListOfficeByKeywordRequest request = REListOfficeByKeywordRequest.newBuilder()
            .setKeyword(Strings.nvl(keyword))
            .addAllOfficeIds(listOfficeId)
            .build();
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.listOfficeByKeyword(request);
    }

    /**
     * Get all MR is linked to the user 's hospital that is logged in by officeUserIds
     *
     * @param officeUserId String of OfficeUser
     * @return list OfficeUser
     */
    public REListUserByOfficeUserIdResponse getAllMRLinkedUserLoggedInByOfficeUserIds(String officeUserId) {
        REGetUserByOfficeUserIdRequest request = REGetUserByOfficeUserIdRequest.newBuilder()
            .setOfficeUserId(Strings.nvl(officeUserId))
            .build();
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getAllMRLinkedUserLoggedInByOfficeUserIds(request);
    }
	public REGetListManagerRequestResponse getListManagerRequest(String departmentId, String jobType,
                                                                 String userName,
                                                                 String officeId, CMNPage cmnPage) {
        REGetListManagerRequestRequest request = REGetListManagerRequestRequest.newBuilder()
            .setDepartmentId(departmentId.equals(DEFAULT_DEPARTMENT_FOR_ALL) ? StringUtils.EMPTY : departmentId)
            .setJobType(jobType.equals(DEFAULT_JOB_TYPE_FOR_ALL) ? StringUtils.EMPTY : jobType)
            .setKeyword(StringUtils.trimToEmpty(userName))
            .setPage(cmnPage)
            .setOfficeId(officeId).build();
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.
            newBlockingStub(registrationChannel);
        return stub.getListManagerRequest(request);

    }

    public REGetStaffRequestByManagerIdResponse getStaffRequestByManagerId(
        REGetStaffRequestByManagerIdRequest request) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getStaffRequestByManagerId(request);
    }

    public List<String> getUserProvisionalList(List<String> officeUserIdList) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);
        REGetUserProvisionalFromListRequest request = REGetUserProvisionalFromListRequest.newBuilder()
            .addAllOfficeUserId(officeUserIdList).build();

        return stub.getUserProvisionalFromList(request).getOfficeUserIdProvisionalList();
    }

    /**
     * check HandlingHospital of DR and MR
     *
     * @param officeId       String of input from DR
     * @param mrOfficeUserId String of input from MR
     * @param mrUserId       String of input from MR
     * @return boolean
     */
    public RECheckHandlingHospitalResponse checkHandlingHospitalRequest(String officeId, String mrOfficeUserId, String mrUserId) {
        RECheckHandlingHospitalRequest request = RECheckHandlingHospitalRequest.newBuilder()
            .addAllOfficeIds(Arrays.asList(Strings.nvl(officeId)))
            .setMrOfficeUserId(Strings.nvl(mrOfficeUserId))
            .setMrUserId(Strings.nvl(mrUserId))
            .build();
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.checkHandlingHospital(request);
    }

    public REListOfficeUserByOfficeIdResponse getListOfficeUserByOfficeId(String officeId, List<String> officeUserIds) {
        RegistrationGrpc.RegistrationBlockingStub
            stub = RegistrationGrpc.newBlockingStub(registrationChannel);

        REListOfficeUserByOfficeIdRequest request = REListOfficeUserByOfficeIdRequest.newBuilder()
            .setOfficeId(officeId)
            .addAllOfficeUserId(officeUserIds)
            .build();
        return stub.getListOfficeUserByOfficeId(request);
    }

    public REGetListDepartmentByOfficeIdResponse getDepartmentParent(Set<String> departmentIds) {

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        REGetListDepartmentByOfficeIdRequest.Builder builder = REGetListDepartmentByOfficeIdRequest.newBuilder();
        builder.addAllDepartmentId(departmentIds);

        return stub.getListDepartmentByOfficeId(builder.build());

    }

    /**
     * get buildings and conference rooms list
     * @return
     */
    public GetBuildingsAndConferenceRoomsResponse getBuildingsAndConferenceRooms() {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        LoginInfo loginInfo = Authoritys.getLoginInfo();

        REGetBuildingsAndConferenceRoomsSettingRequest request = REGetBuildingsAndConferenceRoomsSettingRequest.newBuilder()
            .setOfficeId(loginInfo.getOfficeId())
            .build();

        return new GetBuildingsAndConferenceRoomsResponse(stub.getBuildingsAndConferenceRoomsSetting(request));
    }

    /**
     * save buildings and conference rooms list
     */
    public RESaveBuildingsAndConferenceRoomsSettingResponse saveBuildingsAndConferenceRooms(SaveBuildingsAndConferenceRoomsRequest saveBuildingsAndConferenceRoomsRequest) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        LoginInfo loginInfo = Authoritys.getLoginInfo();

        RESaveBuildingsAndConferenceRoomsSettingRequest request = saveBuildingsAndConferenceRoomsRequest.buildReSaveBuildingsAndConferenceRoomsSettingRequest(loginInfo.getOfficeId());
        return stub.saveBuildingsAndConferenceRoomsSetting(request);
    }

    /**
     *
     * @param conferenceRoomId
     * @return
     */
    public REConferenceRoom getBuildingAndConferenceRoom(String conferenceRoomId) {
        REGetBuildingAndConferenceRoomSettingRequest request = REGetBuildingAndConferenceRoomSettingRequest.newBuilder()
            .setId(conferenceRoomId)
            .build();
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getBuildingAndConferenceRoomSetting(request);
    }

    /**
     * Get list officeUserId by industryIds of office
     * @param industryIds
     * @return
     */
    public REListOfficeUserIdByIndustryResponse getListOfficeUserIdByIndustry(List<String> industryIds) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);

        REListOfficeUserIdByIndustryRequest.Builder request = REListOfficeUserIdByIndustryRequest.newBuilder();
        request.addAllIndustryIds(industryIds);

        return stub.getListOfficeUserIdByIndustry(request.build());
    }

    /**
     * Get list officeUserId by name
     * @param name
     * @param officeUserIds
     * @param officeIds
     * @return
     */
    public REGetMROfficeUserIdByNameResponse getMROfficeUserIdByName(String name, List<String> officeUserIds, List<String> officeIds) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);

        REGetMROfficeUserIdByNameRequest.Builder request = REGetMROfficeUserIdByNameRequest.newBuilder();
        request.setKeyword(Strings.nvl(name));
        request.addAllOfficeUserIds(officeUserIds);
        request.addAllOfficeIds(officeIds);

        return stub.getMROfficeUserIdByName(request.build());
    }

    /**
     * Get list office by name
     * @param name
     * @param officeIds
     * @return
     */
    public REListOfficeByNameResponse getListOfficeByName(String name, List<String> officeIds) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);

        REListOfficeByNameRequest.Builder request = REListOfficeByNameRequest.newBuilder();
        request.setName(Strings.nvl(name));
        request.addAllOfficeIds(officeIds);

        return stub.getListOfficeByName(request.build());
    }

    /**
     * CH0012: Get list MR in Pharmacy when first loading
     *
     * @param page
     * @param size
     * @return
     */
    public PrUsersResponse getListMrByOfficeId(int page, int size, String mrName) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        LoginInfo loginInfo = Authoritys.getLoginInfo();

        REGetListMrByOfficeIdRequest request = REGetListMrByOfficeIdRequest.newBuilder()
            .setOfficeId(Strings.nvl(loginInfo.getOfficeId()))
            .setPage(page)
            .setSize(size)
            .setMrName(mrName).build();
        REGetListMrByOfficeIdResponse response = stub.getListMrByOfficeId(request);

        return new PrUsersResponse(response);
    }

    /**
     * CH0012: Get all MR in Pharmacy
     *
     * @return
     */
    public List<String> getMrOfficeUserIdsByOfficeId() {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        LoginInfo loginInfo = Authoritys.getLoginInfo();

        REGetMrOfficeUserIdsByOfficeIdRequest.Builder request = REGetMrOfficeUserIdsByOfficeIdRequest.newBuilder();
        request.setOfficeId(Strings.nvl(loginInfo.getOfficeId()));
        REGetMrOfficeUserIdsByOfficeIdResponse response = stub.getMrOfficeUserIdsByOfficeId(request.build());

        return response.getOfficeUserIdList();
    }

    /**
     * 機能コードの一覧を得る
     *
     * @param officeId
     * @return
     */
    public REGetFunctionCodesResponse getFunctionCodesByOfficeId(String officeId) {
        REGetFunctionCodesRequest request = REGetFunctionCodesRequest.newBuilder()
            .setOfficeId(officeId)
            .build();

        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        return stub.getFunctionCodesByOfficeId(request);
    }
    public REGetListOfficeUserByConditionResponse getListOfficeUserByCondition(OfficeUserCondition officeUserCondition) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getListOfficeUserByCondition(REGetListOfficeUserByConditionRequest.newBuilder()
            .setLoginOfficeUserId(Strings.nvl(officeUserCondition.getOfficeUserId()))
            .setOfficeId(Strings.nvl(officeUserCondition.getOfficeId()))
            .setDepartmentId(Strings.nvl(officeUserCondition.getDepartmentId()))
            .setKeyword(Strings.nvl(officeUserCondition.getKeyword()))
            .setJobType(Strings.nvl(officeUserCondition.getJobType()))
            .addAllAccountStatus(officeUserCondition.getAccountStatuses())
            .build());
    }

    public REGetOfficeUsersByOfficeIdResponse getListOfficeUserByOfficeId (String officeId) {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        return stub.getOfficeUsersByOfficeId(REGetOfficeUsersByOfficeIdRequest.newBuilder()
            .setOfficeId(Strings.nvl(officeId))
            .build());
    }


    public REGetOfficeForAMResponse getOfficeForAM(String officeId) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub = ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        REGetOfficeForAMRequest.Builder request = REGetOfficeForAMRequest.newBuilder();
        request.setOfficeId(Strings.nvl(officeId));

        return stub.getOfficeForAM(request.build());
    }
    /**
     * Get list office by prefecture
     *
     * @param prefecture
     * @return
     */
    public REListOfficeByPrefectureResponse listOfficeByPrefecture(String prefecture) {
        ExternalRegistrationGrpc.ExternalRegistrationBlockingStub stub =
            ExternalRegistrationGrpc.newBlockingStub(registrationChannel);

        REListOfficeByPrefectureRequest.Builder request = REListOfficeByPrefectureRequest.newBuilder();
        request.setPrefecture(prefecture);

        return stub.listOfficeByPrefecture(request.build());
    }

    //PhucLq
    public List<Student> addStudent(String name, int age, String address, float gpa) {

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        SaveStudentRequest request = SaveStudentRequest.newBuilder()
            .setName(name)
            .setAge(age)
            .setAddress(address)
            .setGpa(gpa)
            .build();
        ListStudentResponse response = stub.getAddStudent(request);
        return response.getStudentList().stream().map(Student::new).collect(Collectors.toList());

    }

    public List<Student> editStudent(String id, String name, int age, String address, float gpa) {

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        EditStudentRequest request = EditStudentRequest.newBuilder()
            .setId(id)
            .setName(name)
            .setAge(age)
            .setAddress(address)
            .setGpa(gpa)
            .build();
        ListStudentResponse response = stub.getEditStudent(request);
        return response.getStudentList().stream().map(Student::new).collect(Collectors.toList());
    }

    public void delStudent(String id) {

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        DelStudentRequest request = DelStudentRequest.newBuilder()
            .setId(id)
            .build();
        stub.getDelStudent(request);
    }

    public List<Student> getSortByNameStudent() {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        ListStudentResponse response = stub.getSortByNameStudent(Empty.getDefaultInstance());
        return response.getStudentList().stream().map(Student::new).collect(Collectors.toList());
    }

    public List<Student> getSortByGpaStudent() {
        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        ListStudentResponse response = stub.getSortByNameStudent(Empty.getDefaultInstance());
        return response.getStudentList().stream().map(Student::new).collect(Collectors.toList());
    }

    public List<Student> showStudent() {

        RegistrationGrpc.RegistrationBlockingStub stub = RegistrationGrpc.newBlockingStub(registrationChannel);
        ListStudentResponse response = stub.getShowStudent(Empty.getDefaultInstance());
        return response.getStudentList().stream().map(Student::new).collect(Collectors.toList());
    }

}
