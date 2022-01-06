import React, {Component} from 'react'
import PastOrdersList from '../PastOrdersList/PastOrdersList'
import './ProfilePage.css'

export default class ProfilePage extends Component {
    constructor(props) {
        super(props)

        this.state = {
            newPassword: "",
            confirmPassword: "",
            pastOrders: [],
            errorMessage: ""
        }

        this.renderRole = () => {
            switch (this.props.user.role) {
                case "ROLE_USER": return "User"
                case "ROLE_MANAGER": return "Manager"
                case "ROLE_ADMIN": return "Admin"
                default: return "User"
            }
        }

        this.setNewPassword = event => {
            this.state.newPassword = event.target.value
        }

        this.setConfirmPassword = event => {
            this.state.confirmPassword = event.target.value
        }

        this.submitForm = async event => {
            event.preventDefault()
            if (this.state.newPassword.length < 4) {
                this.setState({
                    errorMessage: "Password must have at least 4 characters"
                })
                return
            }

            if (this.state.newPassword !== this.state.confirmPassword) {
                this.setState({
                    newPassword: "",
                    confirmPassword: "",
                    errorMessage: "Passwords don't match"
                })
                return
            }

            const uri = `http://localhost:8083/api/proxy/identity/update`
            const updateData = {
                token: this.props.user.token,
                userId: this.props.user.userId,
                password: this.state.newPassword
            }

            const response = await fetch(uri, {
                    method: 'post',
                    headers: {
                        'Content-type': 'application/json'
                    },
                    body: JSON.stringify(updateData)
                }
            )

            if (!response.ok) {
                this.setState({ errorMessage: `Cannot update: response status ${response.status}` })
                return
            }

            const updatedData = await response.json()
            this.props.updateUserToken(updatedData.token)
            this.setState({
                newPassword: "",
                confirmPassword: "",
                errorMessage: "Updated"
            })
        }
    }

    async componentDidMount() {
        const uri = `http://localhost:8081/api/orders/client/${this.props.user.userId}`
        const orderBody = {
            items: this.props.currentOrder
        }
        const res = await fetch(uri, {
            method: 'get',
            headers: {
                'Authorization': `Bearer ${this.props.user.token}`,
            }
        })
        if (!res.ok) {
            console.log(res.statusText)
        }
        const jsonData = await res.json()
        const pastOrdersData = jsonData["_embedded"]["bookOrderList"]
        if (pastOrdersData) {
            this.setState({
                pastOrders: pastOrdersData
            })
        }
    }

    render() {
        return (
            <div className='ProfilePage flexColumn alignCenter'>
                <h1 id="greetingMessage">Welcome, {this.props.user.username}</h1>
                <form id="profileForm" className="flexColumn alignCenter" onSubmit={this.submitForm}>
                    <fieldset className="flexColumn alignCenter">
                        <legend>Personal Information</legend>
                        <div>
                            <label>Username</label>
                            <input type="text" disabled value={this.props.user.username} />
                        </div>
                        <div>
                            <label>Role</label>
                            <input type="text" disabled value={this.renderRole()} />
                        </div>
                        <div>
                            <label>New Password</label>
                            <input type="password" name="password" onChange={this.setNewPassword}/>
                        </div>
                        <div>
                            <label>Confirm Password</label>
                            <input type="password" name="passwordConfirm" onChange={this.setConfirmPassword}/>
                        </div>
                    </fieldset>
                    <div>
                        <input type="submit" className='darkButton' value="Update" />
                    </div>
                    <label>{this.state.errorMessage}</label>
                </form>
                {
                    (this.props.user.role == "ROLE_USER")
                        ? <PastOrdersList user={this.props.user} pastOrders={this.state.pastOrders} />
                        : null
                }
            </div>
        )
    }
}
