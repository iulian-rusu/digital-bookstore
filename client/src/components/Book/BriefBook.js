import React, { Component } from 'react'
import './BriefBook.css'

export default class BriefBook extends Component {
    constructor(props) {
        super(props)

        this.state = {
            quantity: "1"
        }

        this.setQuantity = e => {
            this.setState({
                quantity: e.target.value
            })
        }

        this.orderSelf = () => {
            this.props.orderItem(this.props.book.isbn, this.state.quantity)
        }

        this.openSelfDetails = () => {
            this.props.openDetails(this.props.book.isbn)
        }

        this.removeSelf = () => {
            this.props.removeItem(this.props.book.isbn)
        }

        this.renderButtons = () => {
            switch (this.props.userRole) {
                case "ROLE_USER": return (
                    <>
                        <td><button className='darkButton bookTableButton' onClick={this.orderSelf}>Add</button></td>
                        <td>
                            <input id="quantity" type='text' value={this.state.quantity} onChange={this.setQuantity}></input>
                        </td>
                    </>
                )
                case "ROLE_MANAGER": return (
                    <td><button className='darkButton bookTableButton' onClick={this.removeSelf}>Remove</button></td>
                )
            }
        }
    }

    render() {
        return (
            <tr>
                <td className="bookIsbn" onClick={this.openSelfDetails}><a href="#">{this.props.book.isbn}</a></td>
                <td>{this.props.book.title}</td>
                <td>{this.props.book.authors}</td>
                {this.renderButtons()}
            </tr>
        )
    }
}
