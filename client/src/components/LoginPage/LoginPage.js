import React, { Component } from 'react'
import './LoginPage.css'

export default class LoginPage extends Component {

    constructor(props) {
        super(props)

        this.state = {
            username: "",
            password: "",
            errorMessage: ""
        }

        this.usernamePattern = /[a-zA-Z_]\w+/
        this.passwordPattern = /.{4,}/

        this.onUsernameChange = event => {
            this.setState({ username: event.target.value })
        }

        this.onPasswordChange = event => {
            this.setState({ password: event.target.value })
        }

        this.validateData = () => {
            if (!this.usernamePattern.test(this.state.username)) {
                this.setState({ errorMessage: "Invalid username format" })
                return false
            }
            if (!this.passwordPattern.test(this.state.password)) {
                this.setState({ errorMessage: "Invalid password format" })
                return false
            }
            return true
        }
        this.onLogIn = event => {
            if (this.validateData()) {
                this.props.setToken("TODO: add jwt")
            }
            event.preventDefault()
            return false
        }
    }


    render() {
        return (
            <div className='LoginPage'>
                <form id="loginForm">
                    <input type="text" placeholder="username" autoComplete="off"
                        onChange={this.onUsernameChange} required />
                    <br />
                    <input type="password" placeholder="password" autoComplete="off"
                        onChange={this.onPasswordChange} required />
                    <br />
                    <button className="formButton" onClick={this.onLogIn}>Log In</button>
                </form>
                {this.state.errorMessage ? <h1>{this.state.errorMessage}</h1> : null}
            </div>
        )
    }
}
