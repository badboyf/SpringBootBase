package cn.com.fzk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.fzk.proxy.TestProxy;
import cn.com.fzk.repository.PatientRepository;
import cn.com.fzk.repository.PersonRepository;
import cn.com.fzk.repository.TestRepository;
import cn.com.fzk.request.CreateUserProfileRequest;
import cn.com.fzk.request.PatientInfoRequest;
import cn.com.fzk.request.TestRequest;
import cn.com.fzk.request.ValidateVcodeRequest;
import cn.com.fzk.resource.UserLoginResource;
import cn.com.fzk.response.DefaultResponse;
import cn.com.fzk.response.RegisterResponse;
import cn.com.fzk.util.Constant;
import cn.com.ito.user.schema.generated.tables.pojos.Patient;
import cn.com.ito.user.schema.generated.tables.pojos.Person;
import cn.com.ito.user.schema.generated.tables.pojos.User;

@Service
public class TestService {
  @Autowired
  PatientService patientService;

  @Autowired
  RelationService relationService;

  @Autowired
  MD5andKLService md5andKLService;

  @Autowired
  TestRepository userRepository;

  @Autowired
  PersonRepository personRepository;

  @Autowired
  PatientRepository patientRepository;

  @Autowired
  TestProxy messageProxy;

  @Autowired
  CheckService checkService;

  @Transactional
  public void updateUserProfile(CreateUserProfileRequest request) {
    Person person = checkService.checkPerson(request.getUserId());
    person = setPerson(person, request);
    personRepository.update(person);

    User user = checkService.checkUser(request.getUserId());
    setUser(user, request);
    userRepository.update(user);

    List<Patient> patients = checkService.certainPatientLessThanOne(request.getUserId());
    if (patients.size() == 1) {
      Patient patient = patients.get(0);
      patient = setPatient(patient, request);
      patientRepository.update(patient);
    }
  }

  public void setUser(User user, CreateUserProfileRequest request) {
    if (request.getNickName() != null) {
      user.setNickName(request.getNickName());
    }
    if (request.getUsername() != null) {
      user.setUsername(request.getUsername());
    }
    if (request.getAvatar() != null) {
      user.setAvatar(request.getAvatar());
    }
    if (request.getEmail() != null) {
      user.setEmail(request.getEmail());
    }
    if (request.getPhone() != null) {
      user.setPhone(request.getPhone());
    }
  }

  public void createUserProfile(CreateUserProfileRequest request) {
    checkService.checkUser(request.getUserId());
    Person person = checkService.certainPersonNotExist(request.getUserId());
    person = new Person();
    person = setPerson(person, request);
    personRepository.create(person);
  }

  private Person setPerson(Person person, CreateUserProfileRequest createUserProfileRequest) {
    person.setUserId(createUserProfileRequest.getUserId());
    person.setAddress(createUserProfileRequest.getAddress());
    person.setBirthday(createUserProfileRequest.getBirthday());
    person.setBloodType(createUserProfileRequest.getBloodType());
    person.setCity(createUserProfileRequest.getCity());
    person.setCountry(createUserProfileRequest.getCountry());
    person.setDescription(createUserProfileRequest.getDescription());
    person.setGender(createUserProfileRequest.getGender());
    person.setIdentityId(createUserProfileRequest.getIdentityId());
    person.setProvince(createUserProfileRequest.getProvince());
    person.setName(createUserProfileRequest.getRealName());

    return person;
  }

  @Transactional
  public RegisterResponse register(TestRequest registerRequest) {
    checkService.checkUser(registerRequest.getUserName(), registerRequest.getPhoneNumber());

    User user = setUser(registerRequest);
    int userId = userRepository.create(user);

    String realName = "u" + userId;
    createDefaultPerson(userId, realName);

    createDefaultPatient(userId, realName);

    return new RegisterResponse(userId);
  }

  private User setUser(TestRequest registerRequest) {
    User user = new User();
    user.setAvatar(registerRequest.getAvatar());
    user.setEmail(registerRequest.getEmail());
    user.setNickName(registerRequest.getNickName());
    user.setPassword(md5andKLService.MD5(registerRequest.getPassword()));
    user.setPhone(registerRequest.getPhoneNumber());
    user.setUsername(registerRequest.getUserName());

    return user;
  }

  private void createDefaultPerson(int userId, String realName) {
    CreateUserProfileRequest createUserProfileReq = new CreateUserProfileRequest();
    createUserProfileReq.setUserId(userId);
    createUserProfileReq.setGender(Constant.GENDER_DEFAULT_MAN);
    createUserProfileReq.setIdentityId(Constant.IDENTIFICATION_DEFAULT);
    createUserProfileReq.setRealName(realName);

    createUserProfile(createUserProfileReq);
  }

  private void createDefaultPatient(int userId, String realName) {
    PatientInfoRequest patientInfoReq = new PatientInfoRequest();
    patientInfoReq.setName(realName);
    patientInfoReq.setGender(Constant.GENDER_DEFAULT_MAN);
    patientInfoReq.setIdentificationNo(Constant.IDENTIFICATION_DEFAULT);
    patientInfoReq.setAccountId(String.valueOf(userId));
    patientInfoReq.setRelation("本人");

    addPatient(patientInfoReq);
  }

  public Integer addPatient(PatientInfoRequest patientInfoRequest) {
    int patientId = patientService.createPatient(patientInfoRequest);

    relationService.createRelation(patientId, patientInfoRequest);
    return patientId;
  }

  public UserLoginResource login(String mobileOrEmail, String password) {
    String md5Password = md5andKLService.MD5(password);
    User user = checkService.checkUserPassword(mobileOrEmail, md5Password);
    Person person = personRepository.getPersonByUserId(user.getId());
    UserLoginResource response = setUserLoginResource(person, user);

    return response;
  }

  public UserLoginResource getAccoutProfile(Integer userId) {
    User user = checkService.checkUser(userId);
    Person person = checkService.checkPerson(userId);

    UserLoginResource loginResource = setUserLoginResource(person, user);

    return loginResource;
  }

  public UserLoginResource setUserLoginResource(Person person, User user) {
    UserLoginResource userLoginResource = new UserLoginResource();
    userLoginResource.setUserId(user.getId());
    userLoginResource.setAvatar(user.getAvatar());
    userLoginResource.setEmail(user.getEmail());
    userLoginResource.setMobile(user.getPhone());
    userLoginResource.setNickName(user.getNickName());
    userLoginResource.setUserName(user.getUsername());

    if (person != null) {
      userLoginResource.setAddress(person.getAddress());
      userLoginResource.setBirthday(person.getBirthday());
      userLoginResource.setBloodType(person.getBloodType());
      userLoginResource.setCity(person.getCity());
      userLoginResource.setCountry(person.getCountry());
      userLoginResource.setDescription(person.getDescription());
      userLoginResource.setIdentityId(person.getIdentityId());
      userLoginResource.setGender(person.getGender());
      userLoginResource.setRealName(person.getName());
    }

    return userLoginResource;
  }

  @Transactional
  public void modifyPassword(String newPassword, String phoneNumber, String vCode) {
    DefaultResponse validateResponse = validateVcode(phoneNumber, vCode);
    checkService.checkVcode(validateResponse, vCode);

    User user = checkService.checkUser(phoneNumber);
    user.setPassword(md5andKLService.MD5(newPassword));
    userRepository.update(user);
  }

  public DefaultResponse validateVcode(String phone, String vcode) {
    return messageProxy.validateVcode(new ValidateVcodeRequest(phone, vcode));
  }

  private Patient setPatient(Patient patient, CreateUserProfileRequest createUserProfileRequest) {
    patient.setName(createUserProfileRequest.getRealName());
    patient.setIdentificationNo(createUserProfileRequest.getIdentityId());
    patient.setBirthday(createUserProfileRequest.getBirthday());
    patient.setGender(createUserProfileRequest.getGender());

    return patient;
  }

  public boolean checkUser(String phone) {
    if (userRepository.getByPhone(phone).size() > 0) {
      return true;
    } else {
      return false;
    }
  }
}
