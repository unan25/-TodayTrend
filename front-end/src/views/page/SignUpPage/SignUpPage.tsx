// react
import React, { useEffect, useState } from "react";

// react-router-dom
import { useNavigate } from "react-router-dom";

// redux
import { useDispatch, useSelector } from "react-redux";
import { updateUserInfo } from "../../../redux/_actions/user_action";
import { createAccount } from "../../../redux/_actions/user_action";

// custom hook
import { useUserInfoValidation } from "../../../hook/useUserInfoValidation";
import { useAccountValidation } from "../../../hook/useAccountValidation";

// style
import styles from "./SignUpPage.module.css";
import buttonStyle from "../../../module/styles/button.module.css";
import formStyle from "../../../module/styles/form.module.css";

// State
import { RootState } from "redux/store";
import AccountForm from "../../components/AccountForm/AccountForm";
import UserInfoForm from "../../components/UserInfoForm/UserInfoForm";
import { UserInfo } from "interface/UserInterface";

function SignUpPage() {
  // dispatch & state
  const dispatch = useDispatch<any>();

  // navigate
  const navigate = useNavigate();

  //------------------------------------------------------------------------------

  const [signInStep, setSignInStep] = useState(true);

  const onClickHandler = (e: React.MouseEvent<HTMLButtonElement>) => {
    setSignInStep((prev) => !prev);
  };

  //------------------------------------------------------------------------------

  const {
    Fields: userInfoFields,
    handleChange: userInfoHandleChange,
    Message: userInfoMessage,
    IsValidated: userInfoIsValidated,
  } = useUserInfoValidation();

  const {
    Fields: accountFields,
    handleChange: accountHandleChange,
    Message: accountMessage,
    IsValidated: accountIsValidated,
  } = useAccountValidation();

  // sign-up
  const submitHandler = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    let account = {
      email: accountFields.email,
      password: accountFields.password,
    };

    const response = await dispatch(createAccount(account));

    let userInfo: UserInfo = {
      ...response.payload.UUID_temp,
      ...userInfoFields,
    };

    try {
      dispatch(updateUserInfo(userInfo));
      navigate("/");
    } catch (err: any) {
      console.error(err);
    }
  };

  //------------------------------------------------------------------------------

  return (
    <div className="page-body">
      <form onSubmit={submitHandler} className={formStyle.mainForm}>
        {signInStep ? (
          <AccountForm
            fields={accountFields}
            message={accountMessage}
            handleChange={accountHandleChange}
          />
        ) : (
          <UserInfoForm
            fields={userInfoFields}
            message={userInfoMessage}
            handleChange={userInfoHandleChange}
          />
        )}
        <button
          type="button"
          className={buttonStyle.submitButton}
          onClick={onClickHandler}
          disabled={!accountIsValidated}
        >
          {signInStep ? "다음 단계" : "이전 단계"}
        </button>
        {!signInStep && (
          <button
            type="submit"
            className={buttonStyle.submitButton}
            disabled={!userInfoIsValidated}
          >
            회원가입
          </button>
        )}
      </form>
    </div>
  );
}

export default SignUpPage;
