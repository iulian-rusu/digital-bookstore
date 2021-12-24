import React, { Component } from 'react'
import Order from '../Order/Order'

export default class OrderTable extends Component {
    constructor(props) {
        super(props)

        this.finalizeOrder = () => {
            // post to /api/orders: this.props.user.token, this.props.currentOrder
        }
    }

    render() {
        return (
            <div className='OrderTable flexColumn'>
                <table id='orders' className='styledTable'>
                    <thead>
                        <tr>
                            <th>ISBN</th>
                            <th>Title</th>
                            <th>Quantity</th>
                            <th>Price</th>
                            <th className='smallHeading'></th>
                        </tr>
                    </thead>
                    <tbody>
                        {this.props.currentOrder.map(
                            i => <Order key={i.isbn} item={i} removeSelf={this.props.removeOrderedItem} />
                        )}
                    </tbody>
                </table>
            </div>
        )
    }
}
