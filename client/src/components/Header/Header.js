import React, { Component } from 'react'
import './Header.css'

export default class Header extends Component {
    constructor(props) {
        super(props)

        this.onLogOut = () => {
            this.props.setToken("")
        }

        this.setProfilePage = () => {
            this.props.setCurrentPage("profile")
        }

        this.setBooksPage = () => {
            this.props.setCurrentPage("books")
        }
    }

    render() {
        return (
            <div className='Header'>
                <div id='logoName'>Digital Bookstore</div>
                <div id='menu'>
                    {this.props.token ? <button className='brightButton' onClick={this.setBooksPage}>Books</button> : null}
                    {this.props.token ? <button className='brightButton' onClick={this.setProfilePage}>Profile</button> : null}
                    {this.props.token ? <button className='brightButton' onClick={this.onLogOut}>Log Out</button> : null}
                </div>
            </div>
        )
    }
}
