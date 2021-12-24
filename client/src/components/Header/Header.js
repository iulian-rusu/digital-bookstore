import React, { Component } from 'react'
import './Header.css'

export default class Header extends Component {
    constructor(props) {
        super(props)

        this.onLogOut = () => {
            this.props.setUser(null)
        }

        this.setProfilePage = () => {
            this.props.setCurrentPage("profile")
        }

        this.setBooksPage = () => {
            this.props.setCurrentPage("books")
        }

        this.setCartPage = () => {
            this.props.setCurrentPage("cart")
        }

        this.renderCartButton = () => {
            if (this.props.user.role === "ROLE_USER") {
                return  <button className='brightButton' onClick={this.setCartPage}>Cart</button>
            }
            return null
        }
    }

    render() {
        return (
            <div className='Header flexColumn alignCenter'>
                <div id='logoName'>Digital Bookstore</div>
                <div id='menu'>
                    {this.props.user ? this.renderCartButton() : null}
                    {this.props.user ? <button className='brightButton' onClick={this.setBooksPage}>Books</button> : null}
                    {this.props.user ? <button className='brightButton' onClick={this.setProfilePage}>Profile</button> : null}
                    {this.props.user ? <button className='brightButton' onClick={this.onLogOut}>Log Out</button> : null}
                </div>
            </div>
        )
    }
}
